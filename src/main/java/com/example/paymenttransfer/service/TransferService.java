package com.example.paymenttransfer.service;

import com.example.paymenttransfer.domain.Account;
import com.example.paymenttransfer.domain.BalanceAudit;
import com.example.paymenttransfer.domain.IdempotencyKey;
import com.example.paymenttransfer.domain.Transaction;
import com.example.paymenttransfer.domain.dto.TransferRequestDTO;
import com.example.paymenttransfer.domain.dto.TransferResponseDTO;
import com.example.paymenttransfer.domain.enums.TransactionStatus;
import com.example.paymenttransfer.errors.BankAccountNotFoundException;
import com.example.paymenttransfer.errors.InsufficientFundsException;
import com.example.paymenttransfer.errors.InvalidTransferException;
import com.example.paymenttransfer.repository.AccountRepository;
import com.example.paymenttransfer.repository.BalanceAuditRepository;
import com.example.paymenttransfer.repository.IdempotencyKeyRepository;
import com.example.paymenttransfer.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BalanceAuditRepository balanceAuditRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public TransferResponseDTO transferFunds(TransferRequestDTO request) {
        log.info("Starting transfer: sourceAccountId={}, destinationAccountId={}, amount={}, idempotencyKey={}",
            request.getSourceAccountId(),
            request.getDestinationAccountId(),
            request.getAmount(),
            request.getIdempotencyKey());

        Transaction existingTransaction = checkIdempotency(request.getIdempotencyKey());
        if (existingTransaction != null) {
            log.warn("Duplicate transaction detected with idempotency key: {}", request.getIdempotencyKey());
            return buildResponseFromExistingTransaction(existingTransaction);
        }

        validateTransferRequest(request);

        Account sourceAccount = accountRepository.findByIdWithLock(request.getSourceAccountId())
            .orElseThrow(() -> new BankAccountNotFoundException(request.getSourceAccountId()));

        Account destinationAccount = accountRepository.findByIdWithLock(request.getDestinationAccountId())
            .orElseThrow(() -> new BankAccountNotFoundException(request.getDestinationAccountId()));

        log.debug("Accounts locked: source={}, destination={}",
            sourceAccount.getAccountNumber(),
            destinationAccount.getAccountNumber());

        Transaction transaction = createPendingTransaction(request, sourceAccount, destinationAccount);

        try {
            if (!sourceAccount.hasSufficientFunds(request.getAmount())) {
                throw new InsufficientFundsException(
                    sourceAccount.getId(),
                    request.getAmount(),
                    sourceAccount.getBalance());
            }

            BigDecimal sourceBeforeBalance = sourceAccount.getBalance();
            BigDecimal destinationBeforeBalance = destinationAccount.getBalance();

            sourceAccount.withdrawalFunds(request.getAmount());
            destinationAccount.addFunds(request.getAmount());

            log.info("Transfer executed: {} from account {}, {} to account {}",
                request.getAmount(),
                sourceAccount.getAccountNumber(),
                request.getAmount(),
                destinationAccount.getAccountNumber());

            accountRepository.save(sourceAccount);
            accountRepository.save(destinationAccount);

            transaction.setStatus(TransactionStatus.COMPLETED);
            transaction = transactionRepository.save(transaction);

            createAuditRecord(sourceAccount, sourceBeforeBalance, transaction);
            createAuditRecord(destinationAccount, destinationBeforeBalance, transaction);

            storeIdempotencyKey(request.getIdempotencyKey());

            log.info("Transfer completed successfully: transactionId={}", transaction.getTransactionId());

            return buildSuccessResponse(transaction, sourceAccount, destinationAccount);
        } catch (InsufficientFundsException | InvalidTransferException e) {
            log.error("Transfer failed due to business rule violation: {}", e.getMessage());
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setFailureReason(e.getMessage());
            transactionRepository.save(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during transfer", e);
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setFailureReason("Internal error: " + e.getMessage());
            transactionRepository.save(transaction);
            throw new RuntimeException("Transfer failed due to internal error", e);
        }
    }

    private Transaction checkIdempotency(String idempotencyKey) {
        return transactionRepository.findByIdempotencyKey(idempotencyKey).orElse(null);
    }

    private void validateTransferRequest(TransferRequestDTO request) {
        if (request.getSourceAccountId().equals(request.getDestinationAccountId())) {
            throw new InvalidTransferException("Cannot transfer to the same account");
        }

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransferException("Transfer amount must be greater than zero");
        }
    }

    private Transaction createPendingTransaction(TransferRequestDTO request,
                                                 Account sourceAccount,
                                                 Account destinationAccount) {
        Transaction transaction = Transaction.builder()
            .transactionId(UUID.randomUUID())
            .sourceAccount(sourceAccount)
            .destinationAccount(destinationAccount)
            .amount(request.getAmount())
            .currency(sourceAccount.getCurrency())
            .status(TransactionStatus.PENDING)
            .idempotencyKey(request.getIdempotencyKey())
            .build();

        return transactionRepository.save(transaction);
    }

    private void createAuditRecord(Account account, BigDecimal beforeBalance,
                                   Transaction transaction) {
        BalanceAudit sourceAudit = BalanceAudit.builder()
            .account(account)
            .beforeBalance(beforeBalance)
            .afterBalance(account.getBalance())
            .currency(account.getCurrency())
            .transaction(transaction)
            .build();
        balanceAuditRepository.save(sourceAudit);

        log.debug("Audit records created for transaction {}", transaction.getTransactionId());
    }

    private void storeIdempotencyKey(String keyValue) {
        IdempotencyKey key = IdempotencyKey.builder()
            .keyValue(keyValue)
            .build();
        idempotencyKeyRepository.save(key);
    }

    private TransferResponseDTO buildSuccessResponse(Transaction transaction,
                                                     Account sourceAccount,
                                                     Account destinationAccount) {
        return TransferResponseDTO.builder()
            .transactionId(transaction.getTransactionId())
            .status(TransactionStatus.COMPLETED)
            .message("Transfer completed successfully")
            .details(TransferResponseDTO.TransferDetails.builder()
                .sourceAccountId(sourceAccount.getId())
                .sourceAccountNumber(sourceAccount.getAccountNumber())
                .destinationAccountId(destinationAccount.getId())
                .destinationAccountNumber(destinationAccount.getAccountNumber())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .timestamp(transaction.getCreatedAt())
                .build())
            .build();
    }

    private TransferResponseDTO buildResponseFromExistingTransaction(Transaction transaction) {
        return TransferResponseDTO.builder()
            .transactionId(transaction.getTransactionId())
            .status(transaction.getStatus())
            .message(transaction.getStatus() == TransactionStatus.COMPLETED
                ? "Transfer already processed"
                : "Transfer previously failed")
            .details(TransferResponseDTO.TransferDetails.builder()
                .sourceAccountId(transaction.getSourceAccount().getId())
                .sourceAccountNumber(transaction.getSourceAccount().getAccountNumber())
                .destinationAccountId(transaction.getDestinationAccount().getId())
                .destinationAccountNumber(transaction.getDestinationAccount().getAccountNumber())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .timestamp(transaction.getCreatedAt())
                .failureReason(transaction.getFailureReason())
                .build())
            .build();
    }
}
