package com.prashant.bank.transaction.repository;

import com.prashant.bank.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionReference(
            String transactionReference
    );

    List<Transaction> findByAccountIdOrderByCreatedDateDesc(
            Long accountId
    );

    List<Transaction> findByRelatedAccountIdOrderByCreatedDateDesc(
            Long relatedAccountId
    );

    boolean existsByTransactionReference(
            String transactionReference
    );
}