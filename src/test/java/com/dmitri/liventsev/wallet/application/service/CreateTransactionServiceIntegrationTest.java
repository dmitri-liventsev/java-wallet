package com.dmitri.liventsev.wallet.application.service;

import com.dmitri.liventsev.wallet.domain.model.Transaction;
import com.dmitri.liventsev.wallet.domain.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(properties = "spring.profiles.active=test")
class CreateTransactionServiceIntegrationTest {

    @Autowired
    private CreateTransactionService createTransactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void testCreateTransaction() {
        Transaction transaction = new Transaction();
        transaction.setAmount(100);
        transaction.setAction(Transaction.Action.WIN);
        transaction.setSource(Transaction.Source.GAME);

        Transaction savedTransaction = createTransactionService.createTransaction(transaction);

        assertThat(savedTransaction.getId()).isNotNull();

        Transaction foundTransaction = transactionRepository.findById(savedTransaction.getId()).orElse(null);
        assertThat(foundTransaction).isNotNull();
        assertThat(foundTransaction.getStatus()).isEqualTo(Transaction.Status.NEW);
        assertThat(foundTransaction.getAmount()).isEqualTo(100);
    }
}
