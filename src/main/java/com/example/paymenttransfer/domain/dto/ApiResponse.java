package com.example.paymenttransfer.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response wrapper")
public class ApiResponse<T> {

    @Schema(description = "Indicates if the request was successful", example = "true")
    private boolean success;

    @Schema(description = "Response data (present on success)")
    private T data;

    @Schema(description = "Error messages (present on failure)")
    private List<String> errors;

    @Schema(description = "Response timestamp")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "200")
    private int status;

    @Schema(description = "Human-readable message", example = "Transfer completed successfully")
    private String message;


    /**
     * Create a success response with data
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .timestamp(LocalDateTime.now())
                .status(200)
                .build();
    }

    /**
     * Create a success response with data and custom message
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .timestamp(LocalDateTime.now())
                .status(200)
                .build();
    }

    /**
     * Create a success response with data, message, and custom status
     */
    public static <T> ApiResponse<T> success(T data, String message, int status) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .timestamp(LocalDateTime.now())
                .status(status)
                .build();
    }

    // ========== ERROR RESPONSE FACTORY METHODS ==========

    /**
     * Create an error response with a single error message
     */
    public static <T> ApiResponse<T> error(String error) {
        return ApiResponse.<T>builder()
                .success(false)
                .errors(List.of(error))
                .timestamp(LocalDateTime.now())
                .status(400)
                .build();
    }

    /**
     * Create an error response with a single error and custom status
     */
    public static <T> ApiResponse<T> error(String error, int status) {
        return ApiResponse.<T>builder()
                .success(false)
                .errors(List.of(error))
                .timestamp(LocalDateTime.now())
                .status(status)
                .build();
    }

    /**
     * Create an error response with multiple errors
     */
    public static <T> ApiResponse<T> error(List<String> errors, int status) {
        return ApiResponse.<T>builder()
                .success(false)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .status(status)
                .build();
    }

    /**
     * Create an error response with message and errors
     */
    public static <T> ApiResponse<T> error(String message, List<String> errors, int status) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .status(status)
                .build();
    }
}
