package com.dmitri.liventsev.wallet.domain.service;

import com.dmitri.liventsev.wallet.application.service.CreateTransactionService;
import com.dmitri.liventsev.wallet.domain.model.Correction;
import com.dmitri.liventsev.wallet.domain.model.Transaction;
import com.dmitri.liventsev.wallet.domain.repository.CorrectionRepository;
import com.dmitri.liventsev.wallet.domain.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Profile("!test")
public class CorrectionService {
    private CorrectionService self;

    private final CorrectionRepository correctionRepository;

    private final TransactionRepository transactionRepository;

    private final CreateTransactionService createTransactionService;

    private final ApplicationContext applicationContext;

    private final UUID lockId = UUID.randomUUID();

    public CorrectionService(CorrectionRepository correctionRepository, TransactionRepository transactionRepository, CreateTransactionService createTransactionService, ApplicationContext applicationContext) {
        this.correctionRepository = correctionRepository;
        this.transactionRepository = transactionRepository;
        this.createTransactionService = createTransactionService;
        this.applicationContext = applicationContext;
    }

    @Scheduled(fixedRate = 10000)
    public void performCorrection() {
        correctionRepository.lock(lockId);
        Optional<Correction> correctionOpt = correctionRepository.findByLockId(lockId);
        Correction correction = correctionOpt.orElse(null);
        if (correction != null) {
            try {
                getSelf().doCorrection();
            } finally {
                correction.markAsDone();
                correctionRepository.save(correction);
            }
        }
    }

    private CorrectionService getSelf() {
        if (self == null) {
            self = applicationContext.getBean(CorrectionService.class);
        }

        return self;
    }

    @Transactional
    public void doCorrection() {
        List<Transaction> doomedTransactions = transactionRepository.findOddTransactions(10);

        int amountDelta = 0;
        for (Transaction transaction : doomedTransactions) {
            transaction.setStatus(Transaction.Status.CANCELLED);
            amountDelta -= transaction.getAmount();
        }

        transactionRepository.saveAll(doomedTransactions);
        Transaction transaction = buildCorrectionTransaction(amountDelta);
        createTransactionService.createTransaction(transaction);
    }

    private Transaction buildCorrectionTransaction(int amountDelta) {
        Transaction transaction = new Transaction();

        Transaction.Action action = Transaction.Action.WIN;
        if (amountDelta < 0) {
            action = Transaction.Action.LOST;
        }
        transaction.setAction(action);
        transaction.setAmount(amountDelta);
        transaction.setSource(Transaction.Source.CORRECTION);

        return transaction;
    }

    public void setSelf(CorrectionService self) {
        this.self = self;
    }
}