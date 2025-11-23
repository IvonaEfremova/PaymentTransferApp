package com.example.paymenttransfer.repository;

import com.example.paymenttransfer.domain.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, Long> {

    Optional<IdempotencyKey> findByKeyValue(String keyValue);

    boolean existsByKeyValue(String keyValue);
}
