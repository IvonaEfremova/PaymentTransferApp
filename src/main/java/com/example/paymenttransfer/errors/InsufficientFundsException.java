package com.example.paymenttransfer.errors;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(Long accountId, BigDecimal required, BigDecimal available) {
        super(String.format("Insufficient funds in account %d. Required: %s, Available: %s",
                accountId, required, available));
    }
}
