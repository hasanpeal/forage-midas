package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "${general.kafka-topic}", containerFactory = "kafkaListenerContainerFactory")
public class TransactionListener {

    private static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);
    private final DatabaseConduit databaseConduit;

    public TransactionListener(DatabaseConduit databaseConduit) {
        this.databaseConduit = databaseConduit;
    }

    @KafkaHandler
    public void receive(@Payload Transaction transaction) {
        boolean recorded = databaseConduit.validateAndRecord(
                transaction.getSenderId(), transaction.getRecipientId(), transaction.getAmount()
        );
        logger.info("Received transaction: {}, recorded={}", transaction, recorded);
    }
}


