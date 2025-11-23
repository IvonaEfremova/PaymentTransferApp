package com.example.paymenttransfer.resource;

import com.example.paymenttransfer.domain.dto.ApiResponse;
import com.example.paymenttransfer.domain.dto.TransferRequestDTO;
import com.example.paymenttransfer.domain.dto.TransferResponseDTO;
import com.example.paymenttransfer.resource.annotations.ApiTransferOperation;
import com.example.paymenttransfer.service.TransferService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transfer", description = "Fund transfer operations between accounts")
public class TransferResource {
    private final TransferService transferService;

    @PostMapping
    @ApiTransferOperation
    public ResponseEntity<ApiResponse<TransferResponseDTO>> transfer(@Valid @RequestBody TransferRequestDTO request) {
        log.info("Received transfer request: {}", request);
        TransferResponseDTO response = transferService.transferFunds(request);

        ApiResponse<TransferResponseDTO> apiResponse = ApiResponse.success(
                response,
                "Transfer completed successfully",
                HttpStatus.CREATED.value()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
}
