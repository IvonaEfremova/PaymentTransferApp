package com.example.paymenttransfer.repository;

import com.example.paymenttransfer.domain.BalanceAudit;
import com.example.paymenttransfer.domain.enums.CurrencyEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BalanceAuditRepository extends JpaRepository<BalanceAudit, Long> {

    List<BalanceAudit> findByCurrencyAndAccountId(CurrencyEnum currency, Long accountId);

    @Query("SELECT ba FROM BalanceAudit ba where ba.account.id = :accountId ORDER BY ba.currency, ba.createdAt DESC")
    List<BalanceAudit> findAllByAccount(@Param("accountId") Long accountId);
}
