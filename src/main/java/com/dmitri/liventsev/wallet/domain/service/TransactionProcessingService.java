package com.dmitri.liventsev.wallet.domain.service;

import com.dmitri.liventsev.wallet.domain.model.Transaction;
import com.dmitri.liventsev.wallet.domain.repository.BalanceRepository;
import com.dmitri.liventsev.wallet.domain.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Profile("!test")
public class TransactionProcessingService {
    private TransactionProcessingService self;

    private final TransactionRepository transactionRepository;

    private final BalanceRepository balanceRepository;

    private final ApplicationContext applicationContext;

    private final UUID lockId = UUID.randomUUID();

    public TransactionProcessingService(ApplicationContext applicationContext, BalanceRepository balanceRepository, TransactionRepository transactionRepository) {
        this.applicationContext = applicationContext;
        this.balanceRepository = balanceRepository;
        this.transactionRepository = transactionRepository;
    }

    @Scheduled(fixedRate = 100)
    public void processTransactionsContinuously() {
        processTransactions();
    }

    @Transactional
    protected void processTransactions() {
        transactionRepository.lock(lockId);
        Queue<Transaction> queue = getProcessingTransactions();
        while (!queue.isEmpty() && queue.peek().getLockId().equals(lockId)) {
            Transaction transaction = queue.poll();
            if (transaction == null) {
                continue;
            }
            getSelf().processTransaction(transaction);
        }
    }

    public void processTransaction(Transaction transaction) {
        long currentBalance = balanceRepository.getCurrentBalance();
        long newBalance = currentBalance + transaction.getAmount();

        if (newBalance >= 0 || transaction.getSource() == Transaction.Source.CORRECTION) {
            System.out.println("Initial balance: " + currentBalance + ". Amount: " + transaction.getAmount() + " New balance: " + newBalance);
            balanceRepository.setBalance(newBalance);
            transaction.setStatus(Transaction.Status.DONE);
        } else {
            transaction.setStatus(Transaction.Status.CANCELLED);
        }

        transactionRepository.save(transaction);
    }

    private Queue<Transaction> getProcessingTransactions() {
        List<Transaction> list = transactionRepository.findProcessingTransactionsSortedByCreatedAt();

        Queue<Transaction> queue = new LinkedList<>(list);

        return queue;
    }

    private TransactionProcessingService getSelf() {
        if (self == null) {
            self = applicationContext.getBean(TransactionProcessingService.class);
        }

        return self;
    }

    public void setSelf(TransactionProcessingService self) {
        this.self = self;
    }
}