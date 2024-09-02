package com.dmitri.liventsev.wallet.api.http.controller.request;

import com.dmitri.liventsev.wallet.domain.model.Transaction;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreateTransactionRequest {

    @JsonProperty("state")
    @NotNull(message = "State must not be null")
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

    public void setAmount(String amountStr) {
        try {
            float amountFloat = Float.parseFloat(amountStr);
            this.amount = Math.round(amountFloat * 100);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format");
        }
    }
}
