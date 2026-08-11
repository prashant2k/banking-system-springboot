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
import com.prashant.bank.transaction.repository.TransactionRepository;
import com.prashant.bank.transaction.kafka.TransactionEventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionEventProducer transactionEventProducer;

    @InjectMocks
    private TransactionService transactionService;

    private Account sourceAccount;
    private Account destinationAccount;

    @BeforeEach
    void setUp() {

        sourceAccount = new Account();
        sourceAccount.setAccountId(1L);
        sourceAccount.setBalance(new BigDecimal("1000.00"));
        sourceAccount.setStatus(AccountStatus.ACTIVE);

        destinationAccount = new Account();
        destinationAccount.setAccountId(2L);
        destinationAccount.setBalance(new BigDecimal("500.00"));
        destinationAccount.setStatus(AccountStatus.ACTIVE);
    }

    @Test
    void deposit_shouldIncreaseAccountBalance() {

        TransactionRequestDto request = new TransactionRequestDto(
                1L,
                null,
                TransactionType.DEPOSIT,
                new BigDecimal("500.00"),
                "Cash deposit"
        );

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(sourceAccount));

        when(transactionRepository.existsByTransactionReference(any()))
                .thenReturn(false);

        Transaction savedTransaction = createTransaction(
                TransactionType.DEPOSIT,
                new BigDecimal("500.00"),
                1L,
                null
        );

        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(savedTransaction);

        TransactionResponseDto response =
                transactionService.processTransaction(request);

        assertNotNull(response);
        assertEquals(
                new BigDecimal("1500.00"),
                sourceAccount.getBalance()
        );

        verify(accountRepository).save(sourceAccount);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void withdrawal_shouldDecreaseAccountBalance() {

        TransactionRequestDto request = new TransactionRequestDto(
                1L,
                null,
                TransactionType.WITHDRAWAL,
                new BigDecimal("300.00"),
                "ATM withdrawal"
        );

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(sourceAccount));

        when(transactionRepository.existsByTransactionReference(any()))
                .thenReturn(false);

        Transaction savedTransaction = createTransaction(
                TransactionType.WITHDRAWAL,
                new BigDecimal("300.00"),
                1L,
                null
        );

        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(savedTransaction);

        TransactionResponseDto response =
                transactionService.processTransaction(request);

        assertNotNull(response);

        assertEquals(
                new BigDecimal("700.00"),
                sourceAccount.getBalance()
        );

        verify(accountRepository).save(sourceAccount);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void withdrawal_shouldFailWhenBalanceIsInsufficient() {

        TransactionRequestDto request = new TransactionRequestDto(
                1L,
                null,
                TransactionType.WITHDRAWAL,
                new BigDecimal("1500.00"),
                "Large withdrawal"
        );

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(sourceAccount));

        assertThrows(
                InsufficientBalanceException.class,
                () -> transactionService.processTransaction(request)
        );

        assertEquals(
                new BigDecimal("1000.00"),
                sourceAccount.getBalance()
        );

        verify(accountRepository, never())
                .save(any(Account.class));

        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }

    @Test
    void transfer_shouldDebitSourceAndCreditDestination() {

        TransactionRequestDto request = new TransactionRequestDto(
                1L,
                2L,
                TransactionType.TRANSFER,
                new BigDecimal("400.00"),
                "Transfer"
        );

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(sourceAccount));

        when(accountRepository.findById(2L))
                .thenReturn(Optional.of(destinationAccount));

        when(transactionRepository.existsByTransactionReference(any()))
                .thenReturn(false);

        Transaction savedTransaction = createTransaction(
                TransactionType.TRANSFER,
                new BigDecimal("400.00"),
                1L,
                2L
        );

        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(savedTransaction);

        TransactionResponseDto response =
                transactionService.processTransaction(request);

        assertNotNull(response);

        assertEquals(
                new BigDecimal("600.00"),
                sourceAccount.getBalance()
        );

        assertEquals(
                new BigDecimal("900.00"),
                destinationAccount.getBalance()
        );

        verify(accountRepository).save(sourceAccount);
        verify(accountRepository).save(destinationAccount);

        verify(transactionRepository)
                .save(any(Transaction.class));
    }

    @Test
    void transfer_shouldFailWhenSourceAndDestinationAreSame() {

        TransactionRequestDto request = new TransactionRequestDto(
                1L,
                1L,
                TransactionType.TRANSFER,
                new BigDecimal("100.00"),
                "Invalid transfer"
        );

        InvalidTransactionException exception =
                assertThrows(
                        InvalidTransactionException.class,
                        () -> transactionService.processTransaction(request)
                );

        assertEquals(
                "Source and destination accounts cannot be the same",
                exception.getMessage()
        );

        verifyNoInteractions(accountRepository);
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void deposit_shouldFailForInactiveAccount() {

        sourceAccount.setStatus(AccountStatus.CLOSED);

        TransactionRequestDto request = new TransactionRequestDto(
                1L,
                null,
                TransactionType.DEPOSIT,
                new BigDecimal("100.00"),
                "Deposit"
        );

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(sourceAccount));

        assertThrows(
                InvalidTransactionException.class,
                () -> transactionService.processTransaction(request)
        );

        verify(accountRepository, never())
                .save(any(Account.class));

        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }

    @Test
    void transaction_shouldFailForZeroAmount() {

        TransactionRequestDto request = new TransactionRequestDto(
                1L,
                null,
                TransactionType.DEPOSIT,
                BigDecimal.ZERO,
                "Invalid amount"
        );

        assertThrows(
                InvalidTransactionException.class,
                () -> transactionService.processTransaction(request)
        );

        verifyNoInteractions(accountRepository);
        verifyNoInteractions(transactionRepository);
    }

    private Transaction createTransaction(
            TransactionType type,
            BigDecimal amount,
            Long accountId,
            Long relatedAccountId) {

        Transaction transaction = new Transaction();

        transaction.setTransactionId(100L);
        transaction.setTransactionReference("TXN-TEST-001");
        transaction.setAccountId(accountId);
        transaction.setRelatedAccountId(relatedAccountId);
        transaction.setTransactionType(type);
        transaction.setAmount(amount);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setDescription("Test transaction");

        return transaction;
    }
}