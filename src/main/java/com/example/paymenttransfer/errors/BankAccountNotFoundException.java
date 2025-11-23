package com.example.paymenttransfer.errors;

public class BankAccountNotFoundException extends RuntimeException {
    public BankAccountNotFoundException(Long accountId) {
        super("Bank account not found with ID: " + accountId);
    }
}
