package com.prashant.bank.transaction.kafka;

import com.prashant.bank.transaction.event.TransactionEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class TransactionEventConsumerTest {

    @InjectMocks
    private TransactionEventConsumer transactionEventConsumer;

    @Test
    void consumeTransactionEvent_shouldProcessEventSuccessfully() {

        TransactionEvent event = new TransactionEvent(
                1L,
                100L,
                null,
                "DEPOSIT",
                new BigDecimal("5000.00"),
                "SUCCESS",
                LocalDateTime.now()
        );

        assertDoesNotThrow(() ->
                transactionEventConsumer.consumeTransactionEvent(event)
        );
    }
}