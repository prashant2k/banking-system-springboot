package com.prashant.bank.transaction.event;

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
public class TransactionEvent {

    private Long transactionId;

    private Long accountId;

    private Long relatedAccountId;

    private String transactionType;

    private BigDecimal amount;

    private String status;

    private LocalDateTime transactionDate;
}