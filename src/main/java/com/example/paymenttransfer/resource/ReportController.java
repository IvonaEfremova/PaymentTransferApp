package com.example.paymenttransfer.resource;

import com.example.paymenttransfer.domain.dto.ApiResponse;
import com.example.paymenttransfer.domain.dto.AuditListResponseDTO;
import com.example.paymenttransfer.domain.dto.TransactionListResponseDTO;
import com.example.paymenttransfer.domain.enums.CurrencyEnum;
import com.example.paymenttransfer.resource.annotations.ApiGetAuditsOperation;
import com.example.paymenttransfer.resource.annotations.ApiGetTransactionsOperation;
import com.example.paymenttransfer.service.TransactionReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {
    private final TransactionReportService transactionReportService;

    @GetMapping("/transactions/{id}")
    @ApiGetTransactionsOperation
    public ResponseEntity<ApiResponse<?>> getTransactionsByUser(@PathVariable("id") Long id, @RequestParam(required = false) CurrencyEnum currency) {
        if (currency != null) {
            log.info("Fetching transaction report for currency: {}", currency);
            List<TransactionListResponseDTO> transactions =
                    transactionReportService.getAccountTransactionsByCurrency(id, currency);

            ApiResponse<List<TransactionListResponseDTO>> response =
                    ApiResponse.success(transactions, "Transactions retrieved successfully");

            return ResponseEntity.ok(response);
        } else {
            log.info("Fetching all transactions grouped by currency");
            Map<CurrencyEnum, List<TransactionListResponseDTO>> groupedTransactions =
                    transactionReportService.getAllAccountTransactions(id);
            ApiResponse<Map<CurrencyEnum, List<TransactionListResponseDTO>>> response =
                    ApiResponse.success(groupedTransactions, "Transactions grouped by currency retrieved successfully");

            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/audits/{id}")
    @ApiGetAuditsOperation
    public ResponseEntity<ApiResponse<?>> getAuditsByUser(@PathVariable("id") Long id, @RequestParam(required = false) CurrencyEnum currency) {
        if (currency != null) {
            log.info("Fetching audit report for currency: {}", currency);
            List<AuditListResponseDTO> audits =
                    transactionReportService.getAccountAuditsByCurrency(id, currency);

            ApiResponse<List<AuditListResponseDTO>> response =
                    ApiResponse.success(audits, "Audits retrieved successfully");

            return ResponseEntity.ok(response);
        } else {
            log.info("Fetching all audits grouped by currency");
            Map<CurrencyEnum, List<AuditListResponseDTO>> groupedAudits =
                    transactionReportService.getAllAccountAudits(id);

            ApiResponse<Map<CurrencyEnum, List<AuditListResponseDTO>>> response =
                    ApiResponse.success(groupedAudits, "Audits grouped by currency retrieved successfully");

            return ResponseEntity.ok(response);
        }
    }
}
