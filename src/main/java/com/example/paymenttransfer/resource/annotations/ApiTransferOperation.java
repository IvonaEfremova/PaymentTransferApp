package com.example.paymenttransfer.resource.annotations;

import com.example.paymenttransfer.domain.dto.ErrorResponseDTO;
import com.example.paymenttransfer.domain.dto.TransferResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation for Transfer endpoint documentation
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Initiate a fund transfer",
        description = "Transfer funds from source account to destination account. " +
                "Validates sufficient funds, prevents duplicate transfers using idempotency key, " +
                "and creates audit trail for both accounts."
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "201",
                description = "Transfer completed successfully",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = TransferResponseDTO.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Bad Request - Invalid input, insufficient funds, or validation error",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class)
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Account not found",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class)
                )
        ),
        @ApiResponse(
                responseCode = "409",
                description = "Duplicate transaction - idempotency key already processed",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class)
                )
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class)
                )
        )
})
public @interface ApiTransferOperation {
}

