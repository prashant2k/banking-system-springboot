package com.prashant.bank.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prashant.bank.account.dto.AccountRequestDto;
import com.prashant.bank.account.dto.AccountResponseDto;
import com.prashant.bank.account.entity.AccountStatus;
import com.prashant.bank.account.entity.AccountType;
import com.prashant.bank.account.service.AccountService;
import com.prashant.bank.security.filter.JwtAuthenticationFilter;
import com.prashant.bank.security.service.CustomUserDetailsService;
import com.prashant.bank.security.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AccountController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = JwtAuthenticationFilter.class
                )
        }
)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    // =========================================================
    // CREATE ACCOUNT
    // =========================================================

    @Test
    void createAccount_shouldReturn201() throws Exception {

        AccountRequestDto request = new AccountRequestDto(
                1L,
                AccountType.SAVINGS,
                new BigDecimal("5000.00")
        );

        AccountResponseDto response = createResponse();

        when(accountService.createAccount(
                any(AccountRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/v1/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").value(1))
                .andExpect(jsonPath("$.accountNumber")
                        .value("ACC-100001"))
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.accountType")
                        .value("SAVINGS"))
                .andExpect(jsonPath("$.balance")
                        .value(5000.00))
                .andExpect(jsonPath("$.status")
                        .value("ACTIVE"));

        verify(accountService)
                .createAccount(any(AccountRequestDto.class));
    }

    // =========================================================
    // GET ACCOUNT BY ID
    // =========================================================

    @Test
    void getAccountById_shouldReturn200() throws Exception {

        AccountResponseDto response = createResponse();

        when(accountService.getAccountById(1L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/v1/accounts/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1))
                .andExpect(jsonPath("$.accountNumber")
                        .value("ACC-100001"))
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.accountType")
                        .value("SAVINGS"))
                .andExpect(jsonPath("$.balance")
                        .value(5000.00))
                .andExpect(jsonPath("$.status")
                        .value("ACTIVE"));

        verify(accountService)
                .getAccountById(1L);
    }


    // =========================================================
    // UPDATE ACCOUNT
    // =========================================================

    @Test
    void updateAccount_shouldReturn200() throws Exception {

        AccountRequestDto request = new AccountRequestDto(
                1L,
                AccountType.SAVINGS,
                new BigDecimal("7500.00")
        );

        AccountResponseDto response = createResponse();
        response.setBalance(new BigDecimal("7500.00"));

        when(accountService.updateAccount(
                eq(1L),
                any(AccountRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(
                        put("/api/v1/accounts/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1))
                .andExpect(jsonPath("$.balance")
                        .value(7500.00));

        verify(accountService)
                .updateAccount(
                        eq(1L),
                        any(AccountRequestDto.class)
                );
    }

    // =========================================================
    // CLOSE ACCOUNT
    // =========================================================

    @Test
    void closeAccount_shouldReturn204() throws Exception {

        mockMvc.perform(
                        delete("/api/v1/accounts/1")
                )
                .andExpect(status().isNoContent());

        verify(accountService)
                .closeAccount(1L);
    }

    // =========================================================
    // HELPER
    // =========================================================

    private AccountResponseDto createResponse() {

        AccountResponseDto response =
                new AccountResponseDto();

        response.setAccountId(1L);
        response.setAccountNumber("ACC-100001");
        response.setCustomerId(1L);
        response.setAccountType(AccountType.SAVINGS);
        response.setBalance(new BigDecimal("5000.00"));
        response.setStatus(AccountStatus.ACTIVE);
        response.setCreatedDate(LocalDateTime.now());
        response.setUpdatedDate(LocalDateTime.now());

        return response;
    }
}

