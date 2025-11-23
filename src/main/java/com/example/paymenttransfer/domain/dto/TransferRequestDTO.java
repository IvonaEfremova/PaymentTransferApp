package com.example.paymenttransfer.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload for initiating a fund transfer")
public class TransferRequestDTO {
    @Schema(
            description = "Source account ID (sender)",
            example = "1"
    )
    @NotNull(message = "Source account ID is required")
    private Long sourceAccountId;

    @Schema(
            description = "Destination account ID (receiver)",
            example = "2"
    )
    @NotNull(message = "Destination account ID is required")
    private Long destinationAccountId;

    @Schema(
            description = "Amount to transfer",
            example = "100.50",
            minimum = "0.01"
    )
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 17, fraction = 2, message = "Amount format is invalid")
    private BigDecimal amount;

    @Schema(
            description = "Unique idempotency key to prevent duplicate transfers (use UUID)",
            example = "550e8400-e29b-41d4-a716-446655440000",
            maxLength = 100
    )
    @NotBlank(message = "Idempotency key is required")
    @Size(max = 100, message = "Idempotency key must be less than 100 characters")
    private String idempotencyKey;
}
