package com.mixer.service;

import com.mixer.entity.Transaction;


public interface KafkaProducerService {

    void sendToSchedulerTopic(final Transaction transaction);

}
