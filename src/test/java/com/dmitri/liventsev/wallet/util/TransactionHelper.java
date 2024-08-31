package com.dmitri.liventsev.wallet.util;

import com.dmitri.liventsev.wallet.domain.model.Transaction;
import com.dmitri.liventsev.wallet.domain.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TransactionHelper {
    @Autowired
    TransactionRepository transactionRepository;

    public Transaction createDoneTransaction(int amount) {
        Transaction transaction = new Transaction();

        Transaction.Action action = Transaction.Action.WIN;
        if (amount < 0) {
            amount *= -1;
            action = Transaction.Action.LOST;
        }

        transaction.setAction(action);
        transaction.setAmount(amount);
        transaction.setStatus(Transaction.Status.DONE);
        transaction.setSource(Transaction.Source.WEB);

        return transactionRepository.save(transaction);
    }

    public Transaction createNewTransaction(int amount) {
        Transaction transaction = new Transaction();

        Transaction.Action action = Transaction.Action.WIN;
        if (amount < 0) {
            action = Transaction.Action.LOST;
        }

        transaction.setAction(action);
        transaction.setAmount(amount);
        transaction.setStatus(Transaction.Status.NEW);
        transaction.setSource(Transaction.Source.WEB);

        return transactionRepository.save(transaction);
    }

    public Transaction createNewCorrectionTransaction(int amount) {
        Transaction transaction = new Transaction();

        Transaction.Action action = Transaction.Action.WIN;
        if (amount < 0) {
            action = Transaction.Action.LOST;
        }

        transaction.setAction(action);
        transaction.setAmount(amount);
        transaction.setStatus(Transaction.Status.NEW);
        transaction.setSource(Transaction.Source.CORRECTION);

        return transactionRepository.save(transaction);
    }

    public Transaction createFrozenTransaction(int amount) {
        Transaction transaction = new Transaction();

        Transaction.Action action = Transaction.Action.WIN;
        if (amount < 0) {
            action = Transaction.Action.LOST;
        }

        transaction.setAction(action);
        transaction.setAmount(amount);
        transaction.setStatus(Transaction.Status.PROCESSING);
        transaction.setSource(Transaction.Source.WEB);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime elevenMinutesAgo = now.minusMinutes(11);
        transaction.setLockedAt(elevenMinutesAgo);

        return transactionRepository.save(transaction);
    }
}
