package com.example.paymenttransfer.service;

import com.example.paymenttransfer.domain.Account;
import com.example.paymenttransfer.domain.Transaction;
import com.example.paymenttransfer.domain.dto.TransferRequestDTO;
import com.example.paymenttransfer.domain.dto.TransferResponseDTO;
import com.example.paymenttransfer.domain.enums.CurrencyEnum;
import com.example.paymenttransfer.domain.enums.TransactionStatus;
import com.example.paymenttransfer.errors.InsufficientFundsException;
import com.example.paymenttransfer.errors.InvalidTransferException;
import com.example.paymenttransfer.repository.AccountRepository;
import com.example.paymenttransfer.repository.BalanceAuditRepository;
import com.example.paymenttransfer.repository.IdempotencyKeyRepository;
import com.example.paymenttransfer.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TransferServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BalanceAuditRepository balanceAuditRepository;

    @Mock
    private IdempotencyKeyRepository idempotencyKeyRepository;

    @InjectMocks
    private TransferService transferService;

    @Test
    void transferFunds_successful() {
        Long sourceId = 1L;
        Long destId = 2L;
        BigDecimal amount = BigDecimal.valueOf(100);

        Account source = mock(Account.class);
        Account destination = mock(Account.class);

        when(source.getId()).thenReturn(sourceId);
        when(destination.getId()).thenReturn(destId);
        when(source.getBalance()).thenReturn(BigDecimal.valueOf(200));
        when(destination.getBalance()).thenReturn(BigDecimal.valueOf(50));
        when(source.getCurrency()).thenReturn(CurrencyEnum.EUR);
        when(destination.getCurrency()).thenReturn(CurrencyEnum.EUR);
        when(source.hasSufficientFunds(amount)).thenReturn(true);

        when(accountRepository.findByIdWithLock(sourceId)).thenReturn(Optional.of(source));
        when(accountRepository.findByIdWithLock(destId)).thenReturn(Optional.of(destination));
        when(transactionRepository.findByIdempotencyKey("key-123")).thenReturn(Optional.empty());
        when(transactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TransferRequestDTO request = new TransferRequestDTO();
        request.setSourceAccountId(sourceId);
        request.setDestinationAccountId(destId);
        request.setAmount(amount);
        request.setIdempotencyKey("key-123");

        TransferResponseDTO response = transferService.transferFunds(request);

        assertEquals(TransactionStatus.COMPLETED, response.getStatus());
        verify(accountRepository).save(source);
        verify(accountRepository).save(destination);
        verify(transactionRepository, times(2)).save(any(Transaction.class));
        verify(balanceAuditRepository, times(2)).save(any());
        verify(idempotencyKeyRepository).save(any());
    }

    @Test
    void transferFunds_insufficientFunds_shouldThrow() {
        Long sourceId = 1L;
        Long destId = 2L;
        BigDecimal amount = BigDecimal.valueOf(1000);

        Account source = mock(Account.class);
        Account destination = mock(Account.class);

        when(accountRepository.findByIdWithLock(sourceId)).thenReturn(Optional.of(source));
        when(accountRepository.findByIdWithLock(destId)).thenReturn(Optional.of(destination));
        when(transactionRepository.findByIdempotencyKey("key-123")).thenReturn(Optional.empty());
        when(transactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TransferRequestDTO request = new TransferRequestDTO();
        request.setSourceAccountId(sourceId);
        request.setDestinationAccountId(destId);
        request.setAmount(amount);
        request.setIdempotencyKey("key-123");

        assertThrows(InsufficientFundsException.class, () -> transferService.transferFunds(request));
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    void transferFunds_sameAccount_shouldThrow() {
        Long accountId = 1L;

        TransferRequestDTO request = new TransferRequestDTO();
        request.setSourceAccountId(accountId);
        request.setDestinationAccountId(accountId);
        request.setAmount(BigDecimal.valueOf(50));
        request.setIdempotencyKey("key-123");

        assertThrows(InvalidTransferException.class, () -> transferService.transferFunds(request));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void transferFunds_idempotencyKeyAlreadyUsed_shouldReturnExistingTransaction() {
        Long sourceId = 1L;
        Long destId = 2L;
        Account sourceAccount = mock(Account.class);
        Account destAccount = mock(Account.class);

        when(sourceAccount.getId()).thenReturn(sourceId);
        when(sourceAccount.getAccountNumber()).thenReturn("SRC-123");
        when(destAccount.getId()).thenReturn(destId);
        when(destAccount.getAccountNumber()).thenReturn("DEST-456");

        Transaction existingTransaction = mock(Transaction.class);
        when(existingTransaction.getStatus()).thenReturn(TransactionStatus.COMPLETED);
        when(existingTransaction.getSourceAccount()).thenReturn(sourceAccount);
        when(existingTransaction.getDestinationAccount()).thenReturn(destAccount);
        when(existingTransaction.getTransactionId()).thenReturn(UUID.randomUUID());
        when(existingTransaction.getAmount()).thenReturn(BigDecimal.valueOf(50));
        when(existingTransaction.getCurrency()).thenReturn(CurrencyEnum.EUR);
        when(existingTransaction.getCreatedAt()).thenReturn(java.time.Instant.now());
        when(existingTransaction.getFailureReason()).thenReturn(null);

        when(transactionRepository.findByIdempotencyKey("key-123"))
            .thenReturn(Optional.of(existingTransaction));

        TransferRequestDTO request = new TransferRequestDTO();
        request.setSourceAccountId(sourceId);
        request.setDestinationAccountId(destId);
        request.setAmount(BigDecimal.valueOf(50));
        request.setIdempotencyKey("key-123");

        TransferResponseDTO response = transferService.transferFunds(request);

        assertEquals(TransactionStatus.COMPLETED, response.getStatus());
        verify(transactionRepository, never()).save(any());
    }

}
