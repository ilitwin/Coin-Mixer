package com.mixer.service;

import com.mixer.config.CoinClient;
import com.mixer.entity.Transaction;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class KafkaConsumerService {

    @Value(value = "${min-delay-seconds}")
    private Integer minDelaySeconds;

    @Value(value = "${max-delay-seconds}")
    private Integer maxDelaySeconds;

    @Autowired
    CoinClient coinClient;

    @SneakyThrows
    @KafkaListener(topics = "TRANSACTION_SCHEDULER", groupId = "TRANSACTION_SCHEDULER_GROUP", concurrency = "1")
    public void listenScheduleAction(ConsumerRecord<String, Transaction> cr) {

        var message = cr.value();

        log.info("Message Received From Kafka: {} coins to {} - {}", message.getAmount(), message.getToAddress(), System.nanoTime());

        try {
            // Delay each API call
            randomDelay(minDelaySeconds, maxDelaySeconds);
            log.info("Sending Transaction to Coin: {}", message);
            coinClient.sendTransactionData(message);
        } catch(Exception ex) {
            log.info("A scheduling exception has occurred: " + ex.getMessage());
        }

    }

    void randomDelay(int min, int max) throws InterruptedException {
        int random = (int)(min + max * Math.random());
        Thread.sleep(random * 1000L);
    }

}
