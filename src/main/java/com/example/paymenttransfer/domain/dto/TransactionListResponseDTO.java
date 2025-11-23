package com.example.paymenttransfer.domain.dto;

import com.example.paymenttransfer.domain.enums.CurrencyEnum;
import com.example.paymenttransfer.domain.enums.TransactionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Transaction information in list view")
public class TransactionListResponseDTO {
    @Schema(description = "Unique transaction identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID transactionId;

    @Schema(description = "Source account ID", example = "1")
    private Long sourceAccountId;

    @Schema(description = "Source account number", example = "ACC001")
    private String sourceAccountNumber;

    @Schema(description = "Destination account ID", example = "2")
    private Long destinationAccountId;

    @Schema(description = "Destination account number", example = "ACC002")
    private String destinationAccountNumber;

    @Schema(description = "Transaction amount", example = "100.50")
    private BigDecimal amount;

    @Schema(description = "Transaction currency", example = "EUR")
    private CurrencyEnum currency;

    @Schema(description = "Transaction status", example = "COMPLETED")
    private TransactionStatus status;

    @Schema(description = "Failure reason if transaction failed", example = "Insufficient funds")
    private String failureReason;

    @Schema(description = "Transaction creation timestamp")
    private Instant createdAt;
}
