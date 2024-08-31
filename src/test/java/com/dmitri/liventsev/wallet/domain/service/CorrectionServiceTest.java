package com.dmitri.liventsev.wallet.domain.service;

import com.dmitri.liventsev.wallet.application.service.CreateTransactionService;
import com.dmitri.liventsev.wallet.domain.model.Correction;
import com.dmitri.liventsev.wallet.domain.model.Transaction;
import com.dmitri.liventsev.wallet.domain.repository.CorrectionRepository;
import com.dmitri.liventsev.wallet.domain.repository.TransactionRepository;
import com.dmitri.liventsev.wallet.util.CorrectionHelper;
import com.dmitri.liventsev.wallet.util.TransactionHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.profiles.active=test")
public class CorrectionServiceTest {

    @Autowired
    private CorrectionRepository correctionRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CreateTransactionService createTransactionService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TransactionHelper transactionHelper;

    @Autowired
    private CorrectionHelper correctionHelper;

    private CorrectionService correctionService;

    @BeforeEach
    void setUp() {
        correctionService = new CorrectionService(correctionRepository, transactionRepository, createTransactionService, applicationContext);
        correctionService.setSelf(new CorrectionService(correctionRepository, transactionRepository, createTransactionService, applicationContext));
    }

    @Test
    void testCorrectionIsReady() {
        Correction correction = correctionHelper.createReadyCorrection();

        correctionService.performCorrection();

        Correction updatedCorrection = correctionRepository.findById(1L).orElse(new Correction());
        long differenceInSeconds = Math.abs(ChronoUnit.SECONDS.between(updatedCorrection.getDoneAt(), correction.getDoneAt()));
        assertTrue(differenceInSeconds > 1);
        differenceInSeconds = Math.abs(ChronoUnit.SECONDS.between(updatedCorrection.getLockedAt(), correction.getLockedAt()));
        assertTrue(differenceInSeconds > 1);
    }

    @Test
    void testCorrectionIsNotReady() {
        Correction correction = correctionHelper.createdNotReadyCorrection();

        correctionService.performCorrection();

        Correction updatedCorrection = correctionRepository.findById(1L).orElse(new Correction());

        assertEquals(Correction.Status.READY, correction.getStatus());
        long differenceInSeconds = Math.abs(ChronoUnit.SECONDS.between(updatedCorrection.getDoneAt(), correction.getDoneAt()));
        assertTrue(differenceInSeconds < 2);
        differenceInSeconds = Math.abs(ChronoUnit.SECONDS.between(updatedCorrection.getLockedAt(), correction.getLockedAt()));
        assertTrue(differenceInSeconds < 2);
    }

    @Test
    void testCorrectionIsFrozen() {
        Correction correction = correctionHelper.createFrozenCorrection();

        correctionService.performCorrection();

        Correction updatedCorrection = correctionRepository.findById(1L).orElse(new Correction());

        long differenceInSeconds = Math.abs(ChronoUnit.SECONDS.between(updatedCorrection.getDoneAt(), correction.getDoneAt()));
        assertTrue(differenceInSeconds > 1);
        differenceInSeconds = Math.abs(ChronoUnit.SECONDS.between(updatedCorrection.getLockedAt(), correction.getLockedAt()));
        assertTrue(differenceInSeconds > 1);
    }

    @Test
    void testCorrectionShouldCancelOddTransaction() {
        correctionHelper.createReadyCorrection();
        Transaction oddTransaction = transactionHelper.createDoneTransaction(10);
        transactionHelper.createDoneTransaction(15);

        correctionService.performCorrection();

        oddTransaction = transactionRepository.findById(oddTransaction.getId()).orElse(new Transaction());
        assertEquals(Transaction.Status.CANCELLED, oddTransaction.getStatus());
    }

    @Test
    void testCorrectionShouldNotCancelEvenTransaction() {
        correctionHelper.createReadyCorrection();
        transactionHelper.createDoneTransaction(10);
        Transaction evenTransaction = transactionHelper.createDoneTransaction(15);

        correctionService.performCorrection();

        evenTransaction = transactionRepository.findById(evenTransaction.getId()).orElse(new Transaction());
        assertEquals(Transaction.Status.DONE, evenTransaction.getStatus());
    }

    @Test
    void testCorrectionShouldCreateCorrectionTransaction() {
        correctionHelper.createReadyCorrection();
        transactionHelper.createDoneTransaction(10);
        transactionHelper.createDoneTransaction(15);

        correctionService.performCorrection();

        List<Transaction> transactions = transactionRepository.findAll();
        Transaction correctionTransaction = transactions.get(transactions.size() - 1);
        assertEquals(-10, correctionTransaction.getAmount());
        assertEquals(Transaction.Status.NEW, correctionTransaction.getStatus());
    }
}
