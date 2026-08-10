package com.prashant.bank.account.service;

import com.prashant.bank.account.dto.AccountRequestDto;
import com.prashant.bank.account.dto.AccountResponseDto;
import com.prashant.bank.account.entity.Account;
import com.prashant.bank.account.entity.AccountStatus;
import com.prashant.bank.account.entity.AccountType;
import com.prashant.bank.account.exception.AccountNotFoundException;
import com.prashant.bank.account.repository.AccountRepository;
import com.prashant.bank.customer.entity.Customer;
import com.prashant.bank.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private AccountService accountService;

    private AccountRequestDto request;

    @BeforeEach
    void setUp() {

        request = new AccountRequestDto(
                1L,
                AccountType.SAVINGS,
                new BigDecimal("5000.00")
        );
    }

    @Test
    void createAccount_shouldCreateSuccessfully() {

        Customer customer = new Customer();
        customer.setCustomerId(1L);
        customer.setActive(true);

        Account account = new Account();
        account.setAccountId(10L);
        account.setAccountNumber("ACC10000001");
        account.setCustomerId(1L);
        account.setAccountType(AccountType.SAVINGS);
        account.setBalance(new BigDecimal("5000.00"));
        account.setStatus(AccountStatus.ACTIVE);

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(accountRepository.existsByAccountNumber(any()))
                .thenReturn(false);

        when(accountRepository.save(any(Account.class)))
                .thenReturn(account);

        AccountResponseDto response =
                accountService.createAccount(request);

        assertNotNull(response);
        assertEquals(10L, response.getAccountId());
        assertEquals(1L, response.getCustomerId());
        assertEquals(AccountType.SAVINGS, response.getAccountType());
        assertEquals(
                new BigDecimal("5000.00"),
                response.getBalance()
        );
        assertEquals(AccountStatus.ACTIVE, response.getStatus());

        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void createAccount_shouldThrowExceptionWhenCustomerNotFound() {

        when(customerRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> accountService.createAccount(request)
        );

        verify(accountRepository, never())
                .save(any(Account.class));
    }

    @Test
    void getAccountById_shouldReturnAccount() {

        Account account = new Account();

        account.setAccountId(10L);
        account.setAccountNumber("ACC10000001");
        account.setCustomerId(1L);
        account.setAccountType(AccountType.SAVINGS);
        account.setBalance(new BigDecimal("5000.00"));
        account.setStatus(AccountStatus.ACTIVE);

        when(accountRepository.findById(10L))
                .thenReturn(Optional.of(account));

        AccountResponseDto response =
                accountService.getAccountById(10L);

        assertNotNull(response);
        assertEquals(10L, response.getAccountId());
        assertEquals("ACC10000001", response.getAccountNumber());
    }

    @Test
    void getAccountById_shouldThrowExceptionWhenNotFound() {

        when(accountRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.getAccountById(999L)
        );
    }

    @Test
    void getAccountsByCustomerId_shouldReturnAccounts() {

        Customer customer = new Customer();
        customer.setCustomerId(1L);
        customer.setActive(true);

        Account account = new Account();
        account.setAccountId(10L);
        account.setAccountNumber("ACC10000001");
        account.setCustomerId(1L);
        account.setAccountType(AccountType.SAVINGS);
        account.setBalance(new BigDecimal("5000.00"));
        account.setStatus(AccountStatus.ACTIVE);

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(accountRepository.findByCustomerId(1L))
                .thenReturn(List.of(account));

        List<AccountResponseDto> response =
                accountService.getAccountsByCustomerId(1L);

        assertEquals(1, response.size());
        assertEquals(10L, response.get(0).getAccountId());
    }

    @Test
    void updateAccount_shouldUpdateAccountType() {

        Account account = new Account();

        account.setAccountId(10L);
        account.setAccountNumber("ACC10000001");
        account.setCustomerId(1L);
        account.setAccountType(AccountType.SAVINGS);
        account.setBalance(new BigDecimal("5000.00"));
        account.setStatus(AccountStatus.ACTIVE);

        AccountRequestDto updateRequest =
                new AccountRequestDto(
                        1L,
                        AccountType.CURRENT,
                        new BigDecimal("5000.00")
                );

        when(accountRepository.findById(10L))
                .thenReturn(Optional.of(account));

        when(accountRepository.save(any(Account.class)))
                .thenReturn(account);

        AccountResponseDto response =
                accountService.updateAccount(
                        10L,
                        updateRequest
                );

        assertEquals(
                AccountType.CURRENT,
                response.getAccountType()
        );

        verify(accountRepository).save(account);
    }

    @Test
    void updateAccount_shouldRejectClosedAccount() {

        Account account = new Account();

        account.setAccountId(10L);
        account.setStatus(AccountStatus.CLOSED);

        when(accountRepository.findById(10L))
                .thenReturn(Optional.of(account));

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.updateAccount(
                        10L,
                        request
                )
        );

        verify(accountRepository, never())
                .save(any(Account.class));
    }

    @Test
    void closeAccount_shouldCloseAccount() {

        Account account = new Account();

        account.setAccountId(10L);
        account.setStatus(AccountStatus.ACTIVE);

        when(accountRepository.findById(10L))
                .thenReturn(Optional.of(account));

        when(accountRepository.save(any(Account.class)))
                .thenReturn(account);

        accountService.closeAccount(10L);

        assertEquals(
                AccountStatus.CLOSED,
                account.getStatus()
        );

        verify(accountRepository).save(account);
    }
}