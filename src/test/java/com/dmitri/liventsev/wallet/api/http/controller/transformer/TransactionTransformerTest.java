package com.dmitri.liventsev.wallet.api.http.controller.transformer;

import com.dmitri.liventsev.wallet.api.http.controller.request.CreateTransactionRequest;
import com.dmitri.liventsev.wallet.domain.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionTransformerTest {

    private TransactionTransformer transactionTransformer;

    @BeforeEach
    void setUp() {
        transactionTransformer = new TransactionTransformer();
    }

    @Test
    void testTransform() {
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setAction(Transaction.Action.WIN);
        request.setAmount("100");

        Transaction.Source source = Transaction.Source.WEB;

        Transaction transaction = transactionTransformer.transform(request, source);

        assertEquals(Transaction.Action.WIN, transaction.getAction());
        assertEquals(10000, transaction.getAmount());
        assertEquals(Transaction.Source.WEB, transaction.getSource());
    }

    @Test
    void testPositiveLostTransform() {
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setAction(Transaction.Action.LOST);
        request.setAmount("100");

        assertThrows(IllegalArgumentException.class, ()->{
            transactionTransformer.transform(request, Transaction.Source.WEB);
        });
    }
}
