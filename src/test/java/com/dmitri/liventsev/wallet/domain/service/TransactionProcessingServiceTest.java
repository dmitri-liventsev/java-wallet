package com.dmitri.liventsev.wallet.domain.service;

import com.dmitri.liventsev.wallet.domain.model.Transaction;
import com.dmitri.liventsev.wallet.domain.repository.BalanceRepository;
import com.dmitri.liventsev.wallet.domain.repository.TransactionRepository;
import com.dmitri.liventsev.wallet.util.TransactionHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = "spring.profiles.active=test")
public class TransactionProcessingServiceTest {
    @Autowired
    private TransactionHelper transactionHelper;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private ApplicationContext applicationContext;

    private TransactionProcessingService transactionProcessingService;

    @BeforeEach
    void setUp() {
        transactionProcessingService = new TransactionProcessingService(applicationContext, balanceRepository, transactionRepository);
        transactionProcessingService.setSelf(new TransactionProcessingService(applicationContext, balanceRepository, transactionRepository));
    }

    @Test
    void testNewTransactionShouldBeProcessed() {
        Transaction transaction = transactionHelper.createNewTransaction(10);

        transactionProcessingService.processTransactions();

        transaction = transactionRepository.findById(transaction.getId()).orElse(new Transaction());
        assertEquals(Transaction.Status.DONE, transaction.getStatus());
    }

    @Test
    void testNewTransactionProcessingShouldIncreaseBalance() {
        balanceRepository.setBalance(0);

        transactionHelper.createNewTransaction(10);
        transactionProcessingService.processTransactions();

        assertEquals(10, balanceRepository.getCurrentBalance());
    }

    @Test
    void testNewTransactionProcessingShouldDecreaseBalance() {
        balanceRepository.setBalance(10);

        transactionHelper.createNewTransaction(-10);
        transactionProcessingService.processTransactions();

        assertEquals(0, balanceRepository.getCurrentBalance());
    }

    @Test
    void testFrozenTransactionShouldBeProcessed() {
        balanceRepository.setBalance(10);

        Transaction transaction = transactionHelper.createFrozenTransaction(-10);
        transactionProcessingService.processTransactions();

        transaction = transactionRepository.findById(transaction.getId()).orElse(new Transaction());
        assertEquals(Transaction.Status.DONE, transaction.getStatus());
    }

    @Test
    void testFrozenTransactionProcessingShouldUpdateBalance() {
        balanceRepository.setBalance(10);

        transactionHelper.createFrozenTransaction(-10);
        transactionProcessingService.processTransactions();

        assertEquals(0, balanceRepository.getCurrentBalance());
    }

    @Test
    void testDoneTransactionProcessingShouldNotUpdateBalance() {
        balanceRepository.setBalance(10);

        transactionHelper.createDoneTransaction(-10);
        transactionProcessingService.processTransactions();

        assertEquals(10, balanceRepository.getCurrentBalance());
    }

    @Test
    void testNewtTransactionsProcessingCanNotReduceBalanceLessThenZero() {
        balanceRepository.setBalance(0);

        transactionHelper.createNewTransaction(-10);
        transactionProcessingService.processTransactions();

        assertEquals(0, balanceRepository.getCurrentBalance());
    }

    @Test
    void tesNewtTransactionsProcessingShouldCancelTransactionOnNegativeBalance() {
        balanceRepository.setBalance(0);

        Transaction transaction = transactionHelper.createNewTransaction(-10);
        transactionProcessingService.processTransactions();

        transaction = transactionRepository.findById(transaction.getId()).orElse(new Transaction());
        assertEquals(Transaction.Status.CANCELLED, transaction.getStatus());
    }

    @Test
    void testNewCorrectionTransactionsProcessingCanReduceBalanceLessThenZero() {
        balanceRepository.setBalance(0);

        transactionHelper.createNewCorrectionTransaction(-10);
        transactionProcessingService.processTransactions();

        assertEquals(-10, balanceRepository.getCurrentBalance());
    }
}
