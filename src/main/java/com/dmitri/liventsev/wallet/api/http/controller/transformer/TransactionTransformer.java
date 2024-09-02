package com.dmitri.liventsev.wallet.api.http.controller.transformer;

import com.dmitri.liventsev.wallet.api.http.controller.request.CreateTransactionRequest;
import com.dmitri.liventsev.wallet.domain.model.Transaction;
import org.springframework.stereotype.Service;

@Service
public class TransactionTransformer {
    public Transaction transform(CreateTransactionRequest request, Transaction.Source source) {
        Transaction transaction = new Transaction();
        transaction.setAction(request.getAction());

        transaction.setAmount(request.getAmount());
        if (request.getAction().equals(Transaction.Action.LOST) && request.getAmount() > 0) {
            throw new IllegalArgumentException("Lost transaction amount must be greater than zero");
        }

        transaction.setSource(source);

        return transaction;
    }
}
