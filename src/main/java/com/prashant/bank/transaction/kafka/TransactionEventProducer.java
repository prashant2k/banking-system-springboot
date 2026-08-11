package com.prashant.bank.transaction.kafka;

import com.prashant.bank.config.kafka.KafkaTopics;
import com.prashant.bank.transaction.event.TransactionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionEventProducer {

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public void publishTransactionEvent(TransactionEvent event) {

        kafkaTemplate.send(
                KafkaTopics.TRANSACTION_EVENTS,
                String.valueOf(event.getTransactionId()),
                event
        );
    }
}