package com.prashant.bank.transaction.dto;

import com.prashant.bank.transaction.entity.TransactionStatus;
import com.prashant.bank.transaction.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDto {

    private Long transactionId;

    private String transactionReference;

    private Long accountId;

    private Long relatedAccountId;

    private TransactionType transactionType;

    private BigDecimal amount;

    private TransactionStatus status;

    private String description;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;
}