package com.example.paymenttransfer.repository;

import com.example.paymenttransfer.domain.Transaction;
import com.example.paymenttransfer.domain.enums.CurrencyEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);

    @Query("""
        SELECT t FROM Transaction t
        WHERE (t.sourceAccount.id = :accountId OR t.destinationAccount.id = :accountId)
        and t.currency = :currency
        ORDER BY t.currency, t.createdAt DESC
    """)
    List<Transaction> findByCurrencyAndSourceAccountId(CurrencyEnum currency, Long accountId);

    @Query("""
        SELECT t FROM Transaction t
        WHERE (t.sourceAccount.id = :accountId OR t.destinationAccount.id = :accountId)
        ORDER BY t.currency, t.createdAt DESC
    """)
    List<Transaction> findAllByAccount(@Param("accountId") Long accountId);
}
