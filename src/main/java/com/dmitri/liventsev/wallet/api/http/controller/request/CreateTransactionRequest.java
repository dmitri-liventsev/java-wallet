package com.dmitri.liventsev.wallet.api.http.controller.request;

import com.dmitri.liventsev.wallet.domain.model.Transaction;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreateTransactionRequest {

    @NotNull(message = "Action must not be null")
    private Transaction.Action action;

    @Min(value = 0, message = "Amount must be greater than or equal to zero")
    private int amount;

    public Transaction.Action getAction() {
        return action;
    }

    public void setAction(Transaction.Action action) {
        this.action = action;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}