package com.prashant.bank.transaction.kafka;

import com.prashant.bank.config.kafka.KafkaTopics;
import com.prashant.bank.transaction.event.TransactionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TransactionEventConsumer {

    @KafkaListener(
            topics = KafkaTopics.TRANSACTION_EVENTS,
            groupId = "banking-system-group"
    )
    public void consumeTransactionEvent(
            TransactionEvent event) {

        log.info(
                "Received transaction event: transactionId={}, accountId={}, " +
                        "relatedAccountId={}, transactionType={}, amount={}, " +
                        "status={}, transactionDate={}",
                event.getTransactionId(),
                event.getAccountId(),
                event.getRelatedAccountId(),
                event.getTransactionType(),
                event.getAmount(),
                event.getStatus(),
                event.getTransactionDate()
        );
    }
}