package com.prashant.bank.account.service;

import com.prashant.bank.account.dto.AccountRequestDto;
import com.prashant.bank.account.dto.AccountResponseDto;
import com.prashant.bank.account.entity.Account;
import com.prashant.bank.account.entity.AccountStatus;
import com.prashant.bank.account.exception.AccountNotFoundException;
import com.prashant.bank.account.repository.AccountRepository;
import com.prashant.bank.customer.exception.CustomerNotFoundException;
import com.prashant.bank.customer.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    /**
     * Create a new bank account.
     */
    @Transactional
    public AccountResponseDto createAccount(AccountRequestDto request) {

        // Validate customer exists and is active.
        customerRepository.findById(request.getCustomerId())
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer not found with id: "
                                        + request.getCustomerId()
                        )
                );

        // Prevent negative initial deposits.
        if (request.getInitialDeposit().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Initial deposit cannot be negative"
            );
        }

        // Generate unique account number.
        String accountNumber = generateAccountNumber();

        Account account = new Account();

        account.setAccountNumber(accountNumber);
        account.setCustomerId(request.getCustomerId());
        account.setAccountType(request.getAccountType());
        account.setBalance(request.getInitialDeposit());
        account.setStatus(AccountStatus.ACTIVE);

        Account savedAccount = accountRepository.save(account);

        return mapToResponse(savedAccount);
    }

    /**
     * Get account by ID.
     */
    @Transactional(readOnly = true)
    public AccountResponseDto getAccountById(Long id) {

        Account account = accountRepository.findById(id)
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Account not found with id: " + id
                        )
                );

        return mapToResponse(account);
    }

    /**
     * Get all accounts belonging to a customer.
     */
    @Transactional(readOnly = true)
    public List<AccountResponseDto> getAccountsByCustomerId(
            Long customerId) {

        // Make sure customer exists.
        customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer not found with id: " + customerId
                        )
                );

        return accountRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Update account type.
     *
     * Balance and account number cannot be changed through
     * the normal account update API.
     */
    @Transactional
    public AccountResponseDto updateAccount(
            Long id,
            AccountRequestDto request) {

        Account account = accountRepository.findById(id)
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Account not found with id: " + id
                        )
                );

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new AccountNotFoundException(
                    "Cannot update closed account with id: " + id
            );
        }

        account.setAccountType(request.getAccountType());

        Account updatedAccount = accountRepository.save(account);

        return mapToResponse(updatedAccount);
    }

    /**
     * Close an account.
     *
     * This is a soft delete. The database record remains available
     * for audit/history purposes.
     */
    @Transactional
    public void closeAccount(Long id) {

        Account account = accountRepository.findById(id)
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Account not found with id: " + id
                        )
                );

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new AccountNotFoundException(
                    "Account is already closed with id: " + id
            );
        }

        account.setStatus(AccountStatus.CLOSED);

        accountRepository.save(account);
    }

    /**
     * Generate a unique account number.
     */
    private String generateAccountNumber() {

        String accountNumber;

        do {
            accountNumber = "ACC"
                    + (System.currentTimeMillis() % 100000000L);
        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }

    /**
     * Convert Account entity to response DTO.
     */
    private AccountResponseDto mapToResponse(Account account) {

        AccountResponseDto response = new AccountResponseDto();

        response.setAccountId(account.getAccountId());
        response.setAccountNumber(account.getAccountNumber());
        response.setCustomerId(account.getCustomerId());
        response.setAccountType(account.getAccountType());
        response.setBalance(account.getBalance());
        response.setStatus(account.getStatus());
        response.setCreatedDate(account.getCreatedDate());
        response.setUpdatedDate(account.getUpdatedDate());

        return response;
    }
}