package com.example.paymenttransfer.errors;

public class DuplicateTransactionException extends RuntimeException {
    public DuplicateTransactionException(String idempotencyKey) {
        super("Transaction with idempotency key already processed: " + idempotencyKey);
    }
}
