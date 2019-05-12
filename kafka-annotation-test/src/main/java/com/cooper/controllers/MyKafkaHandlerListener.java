package com.cooper.controllers;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;

/**
 * Listening based on datatype
 */
@KafkaListener
public class MyKafkaHandlerListener {

    @KafkaHandler
    public void listen(String foo) {

    }

    @KafkaHandler
    public void listen(Integer bar) {

    }

    @KafkaHandler(isDefault = true)
    public void listenDefault(Object object) {

    }
}
