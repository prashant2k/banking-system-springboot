package com.prashant.bank.transaction.controller;

import com.prashant.bank.transaction.dto.TransactionRequestDto;
import com.prashant.bank.transaction.dto.TransactionResponseDto;
import com.prashant.bank.transaction.entity.TransactionStatus;
import com.prashant.bank.transaction.entity.TransactionType;
import com.prashant.bank.transaction.exception.InsufficientBalanceException;
import com.prashant.bank.transaction.exception.InvalidTransactionException;
import com.prashant.bank.transaction.exception.TransactionNotFoundException;
import com.prashant.bank.transaction.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {
        TransactionController.class,
        TransactionControllerTest.TestExceptionHandler.class
})
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Test
    void processTransaction_shouldReturn201() throws Exception {

        TransactionResponseDto response = createResponse();

        when(transactionService.processTransaction(
                any(TransactionRequestDto.class)))
                .thenReturn(response);

        String request = """
                {
                    "accountId": 1,
                    "transactionType": "DEPOSIT",
                    "amount": 500.00,
                    "description": "Cash deposit"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/transactions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isCreated());
    }

    @Test
    void processTransaction_shouldReturn400ForInvalidRequest()
            throws Exception {

        String request = """
                {
                    "accountId": 1,
                    "transactionType": "DEPOSIT",
                    "amount": 0,
                    "description": "Invalid amount"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/transactions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTransactionById_shouldReturn200()
            throws Exception {

        TransactionResponseDto response = createResponse();

        when(transactionService.getTransactionById(100L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/v1/transactions/100")
                )
                .andExpect(status().isOk());
    }

    @Test
    void getTransactionById_shouldReturn404WhenNotFound()
            throws Exception {

        when(transactionService.getTransactionById(999L))
                .thenThrow(
                        new TransactionNotFoundException(
                                "Transaction not found with id: 999"
                        )
                );

        mockMvc.perform(
                        get("/api/v1/transactions/999")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void getTransactionsByAccountId_shouldReturn200()
            throws Exception {

        when(transactionService.getTransactionsByAccountId(1L))
                .thenReturn(List.of(createResponse()));

        mockMvc.perform(
                        get("/api/v1/transactions/account/1")
                )
                .andExpect(status().isOk());
    }

    @Test
    void processTransaction_shouldReturn400ForInsufficientBalance()
            throws Exception {

        when(transactionService.processTransaction(
                any(TransactionRequestDto.class)))
                .thenThrow(
                        new InsufficientBalanceException(
                                "Insufficient balance"
                        )
                );

        String request = """
                {
                    "accountId": 1,
                    "transactionType": "WITHDRAWAL",
                    "amount": 5000.00,
                    "description": "Withdrawal"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/transactions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void processTransaction_shouldReturn400ForInvalidTransaction()
            throws Exception {

        when(transactionService.processTransaction(
                any(TransactionRequestDto.class)))
                .thenThrow(
                        new InvalidTransactionException(
                                "Invalid transaction"
                        )
                );

        String request = """
                {
                    "accountId": 1,
                    "transactionType": "TRANSFER",
                    "amount": 100.00,
                    "description": "Invalid transfer"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/transactions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest());
    }

    private TransactionResponseDto createResponse() {

        TransactionResponseDto response =
                new TransactionResponseDto();

        response.setTransactionId(100L);
        response.setTransactionReference("TXN-TEST-001");
        response.setAccountId(1L);
        response.setRelatedAccountId(null);
        response.setTransactionType(TransactionType.DEPOSIT);
        response.setAmount(new BigDecimal("500.00"));
        response.setStatus(TransactionStatus.SUCCESS);
        response.setDescription("Test transaction");
        response.setCreatedDate(LocalDateTime.now());
        response.setUpdatedDate(LocalDateTime.now());

        return response;
    }

    @RestControllerAdvice
    static class TestExceptionHandler {

        @ExceptionHandler(TransactionNotFoundException.class)
        ResponseEntity<Map<String, Object>> handleNotFound(
                TransactionNotFoundException exception) {

            return response(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage()
            );
        }

        @ExceptionHandler(InsufficientBalanceException.class)
        ResponseEntity<Map<String, Object>> handleInsufficientBalance(
                InsufficientBalanceException exception) {

            return response(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage()
            );
        }

        @ExceptionHandler(InvalidTransactionException.class)
        ResponseEntity<Map<String, Object>> handleInvalidTransaction(
                InvalidTransactionException exception) {

            return response(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage()
            );
        }

        private ResponseEntity<Map<String, Object>> response(
                HttpStatus status,
                String message) {

            Map<String, Object> body = new LinkedHashMap<>();

            body.put("timestamp", LocalDateTime.now());
            body.put("status", status.value());
            body.put("error", status.getReasonPhrase());
            body.put("message", message);

            return ResponseEntity
                    .status(status)
                    .body(body);
        }
    }
}