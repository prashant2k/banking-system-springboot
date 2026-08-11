package com.prashant.bank.transaction.controller;

import com.prashant.bank.transaction.dto.TransactionRequestDto;
import com.prashant.bank.transaction.dto.TransactionResponseDto;
import com.prashant.bank.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@AllArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Process deposit, withdrawal, or transfer.
     */
    @PostMapping
    public ResponseEntity<TransactionResponseDto> processTransaction(
            @Valid @RequestBody TransactionRequestDto request) {

        TransactionResponseDto response =
                transactionService.processTransaction(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Get transaction by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDto> getTransactionById(
            @PathVariable Long id) {

        TransactionResponseDto response =
                transactionService.getTransactionById(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Get transaction history for an account.
     */
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponseDto>>
    getTransactionsByAccountId(
            @PathVariable Long accountId) {

        List<TransactionResponseDto> transactions =
                transactionService.getTransactionsByAccountId(accountId);

        return ResponseEntity.ok(transactions);
    }
}