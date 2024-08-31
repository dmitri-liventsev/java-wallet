package com.dmitri.liventsev.wallet.util;

import com.dmitri.liventsev.wallet.domain.model.Correction;
import com.dmitri.liventsev.wallet.domain.model.Transaction;
import com.dmitri.liventsev.wallet.domain.repository.CorrectionRepository;
import com.dmitri.liventsev.wallet.domain.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CorrectionHelper {
    @Autowired
    CorrectionRepository correctionRepository;

    @Autowired
    TransactionRepository transactionRepository;

    public Correction createReadyCorrection() {
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(11);
        Correction correction = correctionRepository.findById(1L).orElse(new Correction());
        correction.setId(1);
        correction.setStatus(Correction.Status.READY);
        correction.setDoneAt(startTime);
        correction.setLockedAt(startTime);
        correctionRepository.save(correction);

        return correction;
    }

    public Correction createdNotReadyCorrection() {
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(1);
        Correction correction = correctionRepository.findById(1L).orElse(new Correction());
        correction.setId(1);
        correction.setStatus(Correction.Status.READY);
        correction.setDoneAt(startTime);
        correction.setLockedAt(startTime);
        correctionRepository.save(correction);

        return correction;
    }

    public Correction createFrozenCorrection() {
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(11);
        Correction correction = correctionRepository.findById(1L).orElse(new Correction());
        correction.setId(1);
        correction.setStatus(Correction.Status.PROCESSING);
        correction.setDoneAt(startTime);
        correction.setLockedAt(startTime);
        correctionRepository.save(correction);

        return correction;
    }

    public Transaction createNewTransaction(int amount) {
        Transaction transaction = new Transaction();

        Transaction.Action action = Transaction.Action.WIN;
        if (amount < 0) {
            amount *= -1;
            action = Transaction.Action.LOST;
        }

        transaction.setAction(action);
        transaction.setAmount(amount);
        transaction.setStatus(Transaction.Status.NEW);
        transaction.setSource(Transaction.Source.WEB);

        return transactionRepository.save(transaction);
    }
}
