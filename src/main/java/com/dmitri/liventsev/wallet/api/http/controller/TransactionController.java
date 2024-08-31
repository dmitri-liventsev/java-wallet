package com.dmitri.liventsev.wallet.api.http.controller;

import com.dmitri.liventsev.wallet.api.http.controller.request.CreateTransactionRequest;
import com.dmitri.liventsev.wallet.api.http.controller.transformer.TransactionTransformer;
import com.dmitri.liventsev.wallet.application.service.CreateTransactionService;
import com.dmitri.liventsev.wallet.domain.model.Transaction;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TransactionController {

    private final CreateTransactionService createTransactionService;
    private final TransactionTransformer transactionTransformer;

    @Autowired
    public TransactionController(CreateTransactionService createTransactionService, TransactionTransformer transactionTransformer) {
        this.createTransactionService = createTransactionService;
        this.transactionTransformer = transactionTransformer;
    }

    @PostMapping("/transaction")
    public ResponseEntity<Transaction> createTransaction(
            @RequestBody CreateTransactionRequest request,
            @RequestHeader("sourceType") String sourceTypeHeader) {

        Transaction.Source source = getSource(sourceTypeHeader);
        Transaction transaction = transactionTransformer.transform(request, source);
        Transaction createdTransaction = createTransactionService.createTransaction(transaction);

        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }

    private Transaction.Source getSource(String sourceType) {
        Transaction.Source source;
        try {
            source = Transaction.Source.valueOf(sourceType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid sourceType value: " + sourceType);
        }

        return source;
    }
}