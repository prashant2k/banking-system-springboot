package com.prashant.bank.account.controller;

import com.prashant.bank.account.dto.AccountRequestDto;
import com.prashant.bank.account.dto.AccountResponseDto;
import com.prashant.bank.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@AllArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * Create a new account.
     */
    @PostMapping
    public ResponseEntity<AccountResponseDto> createAccount(
            @Valid @RequestBody AccountRequestDto request) {

        AccountResponseDto response =
                accountService.createAccount(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Get account by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDto> getAccountById(
            @PathVariable Long id) {

        AccountResponseDto response =
                accountService.getAccountById(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Get all accounts belonging to a customer.
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AccountResponseDto>>
    getAccountsByCustomerId(
            @PathVariable Long customerId) {

        List<AccountResponseDto> accounts =
                accountService.getAccountsByCustomerId(customerId);

        return ResponseEntity.ok(accounts);
    }

    /**
     * Update account type.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AccountResponseDto> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody AccountRequestDto request) {

        AccountResponseDto response =
                accountService.updateAccount(id, request);

        return ResponseEntity.ok(response);
    }

    /**
     * Close account.
     *
     * Performs a soft delete.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> closeAccount(
            @PathVariable Long id) {

        accountService.closeAccount(id);

        return ResponseEntity.noContent().build();
    }
}