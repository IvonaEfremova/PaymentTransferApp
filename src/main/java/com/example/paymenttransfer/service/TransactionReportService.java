package com.example.paymenttransfer.service;

import com.example.paymenttransfer.domain.BalanceAudit;
import com.example.paymenttransfer.domain.Transaction;
import com.example.paymenttransfer.domain.dto.AuditListResponseDTO;
import com.example.paymenttransfer.domain.dto.TransactionListResponseDTO;
import com.example.paymenttransfer.domain.enums.CurrencyEnum;
import com.example.paymenttransfer.repository.BalanceAuditRepository;
import com.example.paymenttransfer.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionReportService {
    private final TransactionRepository transactionRepository;
    private final BalanceAuditRepository balanceAuditRepository;

    @Transactional(readOnly = true)
    public List<TransactionListResponseDTO> getAccountTransactionsByCurrency(Long accountId, CurrencyEnum currency) {
        log.info("Generating transaction report for currency: {}", currency);

        List<Transaction> transactions = transactionRepository.findByCurrencyAndSourceAccountId(currency, accountId);
        return transactions.stream()
            .map(this::mapToTransactionResponse)
            .toList();
    }

    private TransactionListResponseDTO mapToTransactionResponse(Transaction transaction) {
        return TransactionListResponseDTO.builder()
            .transactionId(transaction.getTransactionId())
            .sourceAccountId(transaction.getSourceAccount().getId())
            .sourceAccountNumber(transaction.getSourceAccount().getAccountNumber())
            .destinationAccountId(transaction.getDestinationAccount().getId())
            .destinationAccountNumber(transaction.getDestinationAccount().getAccountNumber())
            .amount(transaction.getAmount())
            .currency(transaction.getCurrency())
            .status(transaction.getStatus())
            .failureReason(transaction.getFailureReason())
            .createdAt(transaction.getCreatedAt())
            .build();
    }

    @Transactional(readOnly = true)
    public Map<CurrencyEnum, List<TransactionListResponseDTO>> getAllAccountTransactions(Long accountId) {
        log.info("Generating comprehensive transaction report grouped by currency");

        List<Transaction> allTransactions = transactionRepository.findAllByAccount(accountId);

        return allTransactions.stream()
            .map(this::mapToTransactionResponse)
            .collect(Collectors.groupingBy(
                TransactionListResponseDTO::getCurrency,
                LinkedHashMap::new,
                Collectors.toList()
            ));
    }

    @Transactional(readOnly = true)
    public List<AuditListResponseDTO> getAccountAuditsByCurrency(Long accountId, CurrencyEnum currency) {
        log.info("Generating audit report for currency: {}", currency);

        List<BalanceAudit> audits = balanceAuditRepository.findByCurrencyAndAccountId(currency, accountId);
        return audits.stream()
            .map(this::mapToAuditResponse)
            .toList();
    }

    private AuditListResponseDTO mapToAuditResponse(BalanceAudit audit) {
        return AuditListResponseDTO.builder()
            .auditId(audit.getId())
            .accountId(audit.getAccount().getId())
            .accountNumber(audit.getAccount().getAccountNumber())
            .beforeBalance(audit.getBeforeBalance())
            .afterBalance(audit.getAfterBalance())
            .currency(audit.getCurrency())
            .transactionId(audit.getTransaction().getTransactionId())
            .createdAt(audit.getCreatedAt())
            .build();
    }

    @Transactional(readOnly = true)
    public Map<CurrencyEnum, List<AuditListResponseDTO>> getAllAccountAudits(Long accountId) {
        log.info("Generating comprehensive audit report grouped by currency");

        List<BalanceAudit> allAudits = balanceAuditRepository.findAllByAccount(accountId);

        return allAudits.stream()
            .map(this::mapToAuditResponse)
            .collect(Collectors.groupingBy(
                AuditListResponseDTO::getCurrency,
                LinkedHashMap::new,
                Collectors.toList()
            ));
    }
}
