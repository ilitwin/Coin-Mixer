package com.mixer.service;

import com.mixer.entity.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;


@Slf4j
@Service
public class KafkaProducerServiceImpl implements KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

        public void sendToSchedulerTopic(Transaction transaction) {

        log.info("Sending Message to Kafka TRANSACTION_SCHEDULER topic: {} - {}", transaction, System.nanoTime());

        this.kafkaTemplate.send("TRANSACTION_SCHEDULER", UUID.randomUUID().toString(), transaction);
    }

}
