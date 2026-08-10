package com.prashant.bank.account.repository;

import com.prashant.bank.account.entity.Account;
import com.prashant.bank.account.entity.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByCustomerId(Long customerId);

    List<Account> findByCustomerIdAndStatus(
            Long customerId,
            AccountStatus status
    );

    boolean existsByAccountNumber(String accountNumber);

    boolean existsByCustomerIdAndAccountTypeAndStatus(
            Long customerId,
            com.prashant.bank.account.entity.AccountType accountType,
            AccountStatus status
    );
}