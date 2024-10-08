package com.dmitri.liventsev.wallet.application.service;

import com.dmitri.liventsev.wallet.domain.model.Transaction;
import com.dmitri.liventsev.wallet.domain.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CreateTransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public CreateTransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction createTransaction(Transaction transaction) {
        transaction.setStatus(Transaction.Status.NEW);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }
}