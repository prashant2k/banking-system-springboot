package com.prashant.bank.transaction.service;

import com.prashant.bank.account.entity.Account;
import com.prashant.bank.account.entity.AccountStatus;
import com.prashant.bank.account.repository.AccountRepository;
import com.prashant.bank.transaction.dto.TransactionRequestDto;
import com.prashant.bank.transaction.dto.TransactionResponseDto;
import com.prashant.bank.transaction.entity.Transaction;
import com.prashant.bank.transaction.entity.TransactionStatus;
import com.prashant.bank.transaction.entity.TransactionType;
import com.prashant.bank.transaction.exception.InsufficientBalanceException;
import com.prashant.bank.transaction.exception.InvalidTransactionException;
import com.prashant.bank.transaction.exception.TransactionNotFoundException;
import com.prashant.bank.transaction.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    /**
     * Process deposit, withdrawal, or transfer.
     */
    @Transactional
    public TransactionResponseDto processTransaction(
            TransactionRequestDto request) {

        validateRequest(request);

        return switch (request.getTransactionType()) {

            case DEPOSIT ->
                    processDeposit(request);

            case WITHDRAWAL ->
                    processWithdrawal(request);

            case TRANSFER ->
                    processTransfer(request);
        };
    }

    /**
     * Deposit money into an account.
     */
    private TransactionResponseDto processDeposit(
            TransactionRequestDto request) {

        Account account = getActiveAccount(request.getAccountId());

        account.setBalance(
                account.getBalance().add(request.getAmount())
        );

        accountRepository.save(account);

        Transaction transaction = createTransaction(
                request,
                account.getAccountId(),
                null,
                TransactionType.DEPOSIT
        );

        return mapToResponse(
                transactionRepository.save(transaction)
        );
    }

    /**
     * Withdraw money from an account.
     */
    private TransactionResponseDto processWithdrawal(
            TransactionRequestDto request) {

        Account account = getActiveAccount(request.getAccountId());

        validateSufficientBalance(
                account,
                request.getAmount()
        );

        account.setBalance(
                account.getBalance().subtract(request.getAmount())
        );

        accountRepository.save(account);

        Transaction transaction = createTransaction(
                request,
                account.getAccountId(),
                null,
                TransactionType.WITHDRAWAL
        );

        return mapToResponse(
                transactionRepository.save(transaction)
        );
    }

    /**
     * Transfer money between two accounts.
     */
    private TransactionResponseDto processTransfer(
            TransactionRequestDto request) {

        if (request.getRelatedAccountId() == null) {
            throw new InvalidTransactionException(
                    "Destination account is required for transfer"
            );
        }

        if (request.getAccountId()
                .equals(request.getRelatedAccountId())) {

            throw new InvalidTransactionException(
                    "Source and destination accounts cannot be the same"
            );
        }

        Account sourceAccount =
                getActiveAccount(request.getAccountId());

        Account destinationAccount =
                getActiveAccount(request.getRelatedAccountId());

        validateSufficientBalance(
                sourceAccount,
                request.getAmount()
        );

        /*
         * Debit source account.
         */
        sourceAccount.setBalance(
                sourceAccount.getBalance()
                        .subtract(request.getAmount())
        );

        /*
         * Credit destination account.
         */
        destinationAccount.setBalance(
                destinationAccount.getBalance()
                        .add(request.getAmount())
        );

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        /*
         * One transaction record represents the transfer.
         */
        Transaction transaction = createTransaction(
                request,
                sourceAccount.getAccountId(),
                destinationAccount.getAccountId(),
                TransactionType.TRANSFER
        );

        return mapToResponse(
                transactionRepository.save(transaction)
        );
    }

    /**
     * Get transaction by ID.
     */
    @Transactional(readOnly = true)
    public TransactionResponseDto getTransactionById(Long id) {

        Transaction transaction =
                transactionRepository.findById(id)
                        .orElseThrow(() ->
                                new TransactionNotFoundException(
                                        "Transaction not found with id: "
                                                + id
                                )
                        );

        return mapToResponse(transaction);
    }

    /**
     * Get transaction history for an account.
     */
    @Transactional(readOnly = true)
    public List<TransactionResponseDto> getTransactionsByAccountId(
            Long accountId) {

        getActiveAccount(accountId);

        return transactionRepository
                .findByAccountIdOrderByCreatedDateDesc(accountId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Validate transaction request.
     */
    private void validateRequest(
            TransactionRequestDto request) {

        if (request == null) {
            throw new InvalidTransactionException(
                    "Transaction request cannot be null"
            );
        }

        if (request.getAmount() == null ||
                request.getAmount()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            throw new InvalidTransactionException(
                    "Transaction amount must be greater than zero"
            );
        }

        if (request.getTransactionType() == null) {
            throw new InvalidTransactionException(
                    "Transaction type is required"
            );
        }

        if (request.getAccountId() == null) {
            throw new InvalidTransactionException(
                    "Account ID is required"
            );
        }
    }

    /**
     * Find an active account.
     */
    private Account getActiveAccount(Long accountId) {

        Account account =
                accountRepository.findById(accountId)
                        .orElseThrow(() ->
                                new TransactionNotFoundException(
                                        "Account not found with id: "
                                                + accountId
                                )
                        );

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidTransactionException(
                    "Account is not active: " + accountId
            );
        }

        return account;
    }

    /**
     * Ensure account has enough funds.
     */
    private void validateSufficientBalance(
            Account account,
            BigDecimal amount) {

        if (account.getBalance()
                .compareTo(amount) < 0) {

            throw new InsufficientBalanceException(
                    "Insufficient balance for account: "
                            + account.getAccountId()
            );
        }
    }

    /**
     * Create transaction entity.
     */
    private Transaction createTransaction(
            TransactionRequestDto request,
            Long accountId,
            Long relatedAccountId,
            TransactionType transactionType) {

        Transaction transaction = new Transaction();

        transaction.setTransactionReference(
                generateTransactionReference()
        );

        transaction.setAccountId(accountId);
        transaction.setRelatedAccountId(relatedAccountId);
        transaction.setTransactionType(transactionType);
        transaction.setAmount(request.getAmount());
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setDescription(request.getDescription());

        return transaction;
    }

    /**
     * Generate unique transaction reference.
     */
    private String generateTransactionReference() {

        String reference;

        do {
            reference = "TXN-" +
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 20)
                            .toUpperCase();

        } while (
                transactionRepository
                        .existsByTransactionReference(reference)
        );

        return reference;
    }

    /**
     * Map entity to response DTO.
     */
    private TransactionResponseDto mapToResponse(
            Transaction transaction) {

        TransactionResponseDto response =
                new TransactionResponseDto();

        response.setTransactionId(
                transaction.getTransactionId()
        );

        response.setTransactionReference(
                transaction.getTransactionReference()
        );

        response.setAccountId(
                transaction.getAccountId()
        );

        response.setRelatedAccountId(
                transaction.getRelatedAccountId()
        );

        response.setTransactionType(
                transaction.getTransactionType()
        );

        response.setAmount(
                transaction.getAmount()
        );

        response.setStatus(
                transaction.getStatus()
        );

        response.setDescription(
                transaction.getDescription()
        );

        response.setCreatedDate(
                transaction.getCreatedDate()
        );

        response.setUpdatedDate(
                transaction.getUpdatedDate()
        );

        return response;
    }
}