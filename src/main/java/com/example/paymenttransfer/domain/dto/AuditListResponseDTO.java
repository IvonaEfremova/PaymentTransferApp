package com.example.paymenttransfer.domain.dto;

import com.example.paymenttransfer.domain.enums.CurrencyEnum;
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
@Schema(description = "Balance audit record showing before/after balance changes")
public class AuditListResponseDTO {
    @Schema(description = "Audit record ID", example = "1")
    private Long auditId;

    @Schema(description = "Account ID", example = "1")
    private Long accountId;

    @Schema(description = "Account number", example = "ACC001")
    private String accountNumber;

    @Schema(description = "Balance before transaction", example = "1000.00")
    private BigDecimal beforeBalance;

    @Schema(description = "Balance after transaction", example = "900.00")
    private BigDecimal afterBalance;

    @Schema(description = "Currency", example = "EUR")
    private CurrencyEnum currency;

    @Schema(description = "Related transaction ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID transactionId;

    @Schema(description = "Audit record creation timestamp")
    private Instant createdAt;
}
