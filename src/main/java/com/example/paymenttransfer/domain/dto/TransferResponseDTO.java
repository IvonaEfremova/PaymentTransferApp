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
@Schema(description = "Response after initiating a transfer")
public class TransferResponseDTO {
    @Schema(
        description = "Unique transaction identifier",
        example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private UUID transactionId;

    @Schema(
        description = "Transaction status",
        example = "COMPLETED",
        allowableValues = {"PENDING", "COMPLETED", "FAILED"}
    )
    private TransactionStatus status;

    @Schema(
        description = "Human-readable message about the transfer",
        example = "Transfer completed successfully"
    )
    private String message;

    @Schema(description = "Detailed transfer information")
    private TransferDetails details;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TransferDetails {
        @Schema(description = "Source account ID", example = "1")
        private Long sourceAccountId;

        @Schema(description = "Source account number", example = "ACC001")
        private String sourceAccountNumber;

        @Schema(description = "Destination account ID", example = "2")
        private Long destinationAccountId;

        @Schema(description = "Destination account number", example = "ACC002")
        private String destinationAccountNumber;

        @Schema(description = "Transfer amount", example = "1000")
        private BigDecimal amount;

        @Schema(description = "Currency of the transfer", example = "EUR")
        private CurrencyEnum currency;

        @Schema(description = "Timestamp when transfer was created")
        private Instant timestamp;

        @Schema(description = "Failure reason (only present if status is FAILED)", example = "Insufficient funds")
        private String failureReason;
    }
}
