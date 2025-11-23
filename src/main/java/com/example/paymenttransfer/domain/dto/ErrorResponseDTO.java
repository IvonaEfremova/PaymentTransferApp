package com.example.paymenttransfer.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard error response")
public class ErrorResponseDTO {
    @Schema(description = "Timestamp when error occurred")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Error type", example = "Insufficient Funds")
    private String error;

    @Schema(description = "Detailed error message",
            example = "Insufficient funds in account 1. Required: 100.00, Available: 50.00")
    private String message;

    @Schema(description = "Request path that caused the error", example = "/api/transfers")
    private String path;

    @Schema(description = "Validation errors (field -> error message)")
    private Map<String, String> validationErrors;
}
