package com.dmitri.liventsev.wallet.domain.service;

import com.dmitri.liventsev.wallet.domain.model.Transaction;
import com.dmitri.liventsev.wallet.domain.repository.BalanceRepository;
import com.dmitri.liventsev.wallet.domain.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Stack;
import java.util.UUID;

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
    
    protected void processTransactions() {
        transactionRepository.lock(lockId);
        Stack<Transaction> stack = getProcessingTransactions();

        while (!stack.isEmpty() && stack.peek().getLockId().equals(lockId)) {
            Transaction transaction = stack.pop();
            getSelf().processTransaction(transaction);
        }
    }

    @Transactional
    public void processTransaction(Transaction transaction) {
        long currentBalance = balanceRepository.getCurrentBalance();
        long newBalance = currentBalance + transaction.getAmount();

        if (newBalance >= 0 || transaction.getSource() == Transaction.Source.CORRECTION) {
            balanceRepository.setBalance(newBalance);
            transaction.setStatus(Transaction.Status.DONE);
        } else {
            transaction.setStatus(Transaction.Status.CANCELLED);
        }

        transactionRepository.save(transaction);
    }

    private Stack<Transaction> getProcessingTransactions() {
        List<Transaction> list = transactionRepository.findProcessingTransactionsSortedByCreatedAt();

        Stack<Transaction> stack = new Stack<>();
        stack.addAll(list);

        return stack;
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