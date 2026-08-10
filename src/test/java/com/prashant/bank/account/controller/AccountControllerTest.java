package com.prashant.bank.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prashant.bank.account.dto.AccountRequestDto;
import com.prashant.bank.account.dto.AccountResponseDto;
import com.prashant.bank.account.entity.AccountStatus;
import com.prashant.bank.account.entity.AccountType;
import com.prashant.bank.account.service.AccountService;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    @Test
    void createAccount_shouldReturn201() throws Exception {

        AccountRequestDto request =
                new AccountRequestDto(
                        1L,
                        AccountType.SAVINGS,
                        new BigDecimal("5000.00")
                );

        AccountResponseDto response =
                new AccountResponseDto(
                        10L,
                        "ACC10000001",
                        1L,
                        AccountType.SAVINGS,
                        new BigDecimal("5000.00"),
                        AccountStatus.ACTIVE,
                        null,
                        null
                );

        when(accountService.createAccount(any(AccountRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/v1/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").value(10))
                .andExpect(jsonPath("$.accountNumber")
                        .value("ACC10000001"))
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.accountType")
                        .value("SAVINGS"))
                .andExpect(jsonPath("$.balance").value(5000.00))
                .andExpect(jsonPath("$.status")
                        .value("ACTIVE"));

        verify(accountService).createAccount(
                any(AccountRequestDto.class)
        );
    }

    @Test
    void createAccount_shouldReturn400ForInvalidRequest()
            throws Exception {

        AccountRequestDto request =
                new AccountRequestDto(
                        null,
                        null,
                        null
                );

        mockMvc.perform(
                        post("/api/v1/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(accountService, never())
                .createAccount(any(AccountRequestDto.class));
    }

    @Test
    void getAccountById_shouldReturn200() throws Exception {

        AccountResponseDto response =
                new AccountResponseDto(
                        10L,
                        "ACC10000001",
                        1L,
                        AccountType.SAVINGS,
                        new BigDecimal("5000.00"),
                        AccountStatus.ACTIVE,
                        null,
                        null
                );

        when(accountService.getAccountById(10L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/v1/accounts/10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(10))
                .andExpect(jsonPath("$.accountNumber")
                        .value("ACC10000001"));

        verify(accountService).getAccountById(10L);
    }

    @Test
    void getAccountsByCustomerId_shouldReturn200()
            throws Exception {

        AccountResponseDto response =
                new AccountResponseDto(
                        10L,
                        "ACC10000001",
                        1L,
                        AccountType.SAVINGS,
                        new BigDecimal("5000.00"),
                        AccountStatus.ACTIVE,
                        null,
                        null
                );

        when(accountService.getAccountsByCustomerId(1L))
                .thenReturn(List.of(response));

        mockMvc.perform(
                        get("/api/v1/accounts/customer/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountId").value(10))
                .andExpect(jsonPath("$[0].customerId").value(1));

        verify(accountService)
                .getAccountsByCustomerId(1L);
    }

    @Test
    void updateAccount_shouldReturn200() throws Exception {

        AccountRequestDto request =
                new AccountRequestDto(
                        1L,
                        AccountType.CURRENT,
                        new BigDecimal("5000.00")
                );

        AccountResponseDto response =
                new AccountResponseDto(
                        10L,
                        "ACC10000001",
                        1L,
                        AccountType.CURRENT,
                        new BigDecimal("5000.00"),
                        AccountStatus.ACTIVE,
                        null,
                        null
                );

        when(accountService.updateAccount(
                eq(10L),
                any(AccountRequestDto.class)
        )).thenReturn(response);

        mockMvc.perform(
                        put("/api/v1/accounts/10")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountType")
                        .value("CURRENT"));

        verify(accountService).updateAccount(
                eq(10L),
                any(AccountRequestDto.class)
        );
    }

    @Test
    void closeAccount_shouldReturn204() throws Exception {

        doNothing()
                .when(accountService)
                .closeAccount(10L);

        mockMvc.perform(
                        delete("/api/v1/accounts/10")
                )
                .andExpect(status().isNoContent());

        verify(accountService).closeAccount(10L);
    }
}