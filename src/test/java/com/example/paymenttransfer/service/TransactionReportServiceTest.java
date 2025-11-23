package com.example.paymenttransfer.service;

import com.example.paymenttransfer.domain.Account;
import com.example.paymenttransfer.domain.BalanceAudit;
import com.example.paymenttransfer.domain.Transaction;
import com.example.paymenttransfer.domain.dto.AuditListResponseDTO;
import com.example.paymenttransfer.domain.dto.TransactionListResponseDTO;
import com.example.paymenttransfer.domain.enums.CurrencyEnum;
import com.example.paymenttransfer.domain.enums.TransactionStatus;
import com.example.paymenttransfer.repository.BalanceAuditRepository;
import com.example.paymenttransfer.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionReportServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BalanceAuditRepository balanceAuditRepository;

    @InjectMocks
    private TransactionReportService reportService;

    private Account sourceAccount;
    private Account destinationAccount;

    @BeforeEach
    void setUp() {
        sourceAccount = Account.builder()
            .id(1L)
            .accountNumber("SRC123")
            .currency(CurrencyEnum.EUR)
            .build();

        destinationAccount = Account.builder()
            .id(2L)
            .accountNumber("DST456")
            .currency(CurrencyEnum.EUR)
            .build();
    }

    @Test
    void getAccountTransactionsByCurrency_returnsMappedTransactions() {
        Transaction tx = Transaction.builder()
            .transactionId(UUID.randomUUID())
            .sourceAccount(sourceAccount)
            .destinationAccount(destinationAccount)
            .amount(BigDecimal.valueOf(100))
            .currency(CurrencyEnum.EUR)
            .status(TransactionStatus.COMPLETED)
            .createdAt(Instant.now())
            .build();

        when(transactionRepository.findByCurrencyAndSourceAccountId(CurrencyEnum.EUR, 1L))
            .thenReturn(List.of(tx));

        List<TransactionListResponseDTO> result = reportService.getAccountTransactionsByCurrency(1L, CurrencyEnum.EUR);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTransactionId()).isEqualTo(tx.getTransactionId());
        assertThat(result.get(0).getAmount()).isEqualByComparingTo(tx.getAmount());
        assertThat(result.get(0).getStatus()).isEqualTo(TransactionStatus.COMPLETED);
    }

    @Test
    void getAllAccountTransactions_groupsByCurrency() {
        Transaction tx1 = Transaction.builder()
            .transactionId(UUID.randomUUID())
            .sourceAccount(sourceAccount)
            .destinationAccount(destinationAccount)
            .amount(BigDecimal.valueOf(50))
            .currency(CurrencyEnum.EUR)
            .status(TransactionStatus.COMPLETED)
            .createdAt(Instant.now())
            .build();

        Transaction tx2 = Transaction.builder()
            .transactionId(UUID.randomUUID())
            .sourceAccount(sourceAccount)
            .destinationAccount(destinationAccount)
            .amount(BigDecimal.valueOf(70))
            .currency(CurrencyEnum.MKD)
            .status(TransactionStatus.COMPLETED)
            .createdAt(Instant.now())
            .build();

        when(transactionRepository.findAllByAccount(1L))
            .thenReturn(List.of(tx1, tx2));

        Map<CurrencyEnum, List<TransactionListResponseDTO>> result = reportService.getAllAccountTransactions(1L);

        assertThat(result.keySet()).containsExactlyInAnyOrder(CurrencyEnum.MKD, CurrencyEnum.EUR);
        assertThat(result.get(CurrencyEnum.EUR)).hasSize(1);
        assertThat(result.get(CurrencyEnum.MKD)).hasSize(1);
    }

    @Test
    void getAccountAuditsByCurrency_returnsMappedAudits() {
        BalanceAudit audit = BalanceAudit.builder()
            .id(10L)
            .account(sourceAccount)
            .beforeBalance(BigDecimal.valueOf(200))
            .afterBalance(BigDecimal.valueOf(100))
            .currency(CurrencyEnum.EUR)
            .transaction(Transaction.builder().transactionId(UUID.randomUUID()).build())
            .createdAt(Instant.now())
            .build();

        when(balanceAuditRepository.findByCurrencyAndAccountId(CurrencyEnum.EUR, 1L))
            .thenReturn(List.of(audit));

        List<AuditListResponseDTO> result = reportService.getAccountAuditsByCurrency(1L, CurrencyEnum.EUR);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAuditId()).isEqualTo(audit.getId());
        assertThat(result.get(0).getBeforeBalance()).isEqualByComparingTo(audit.getBeforeBalance());
    }

    @Test
    void getAllAccountAudits_groupsByCurrency() {
        BalanceAudit audit1 = BalanceAudit.builder()
            .id(1L)
            .account(sourceAccount)
            .beforeBalance(BigDecimal.valueOf(200))
            .afterBalance(BigDecimal.valueOf(100))
            .currency(CurrencyEnum.USD)
            .transaction(Transaction.builder().transactionId(UUID.randomUUID()).build())
            .createdAt(Instant.now())
            .build();

        BalanceAudit audit2 = BalanceAudit.builder()
            .id(2L)
            .account(sourceAccount)
            .beforeBalance(BigDecimal.valueOf(300))
            .afterBalance(BigDecimal.valueOf(200))
            .currency(CurrencyEnum.EUR)
            .transaction(Transaction.builder().transactionId(UUID.randomUUID()).build())
            .createdAt(Instant.now())
            .build();

        when(balanceAuditRepository.findAllByAccount(1L)).thenReturn(List.of(audit1, audit2));

        Map<CurrencyEnum, List<AuditListResponseDTO>> result = reportService.getAllAccountAudits(1L);

        assertThat(result.keySet()).containsExactlyInAnyOrder(CurrencyEnum.USD, CurrencyEnum.EUR);
        assertThat(result.get(CurrencyEnum.USD)).hasSize(1);
        assertThat(result.get(CurrencyEnum.EUR)).hasSize(1);
    }

}
