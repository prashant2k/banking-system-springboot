package com.prashant.bank.account.dto;

import com.prashant.bank.account.entity.AccountStatus;
import com.prashant.bank.account.entity.AccountType;
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
public class AccountResponseDto {

    private Long accountId;

    private String accountNumber;

    private Long customerId;

    private AccountType accountType;

    private BigDecimal balance;

    private AccountStatus status;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;
}