package com.cooper.controllers;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;

import java.util.List;

public class MyBatchListener {

    @KafkaListener(id = "list", topics = "myTopic", containerFactory = "batchFactory")
    public void listen(List<String> list) {
        System.out.println(list);
    }

    @KafkaListener(id = "list2", topics = "myTopic", containerFactory = "batchFactory")
    public void listen(List<String> list,
                       @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<Integer> keys,
                       @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) List<String> topics,
                       @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        System.out.println(keys);
    }

    @KafkaListener(id = "listMsg", topics = "myTopic", containerFactory = "batchFactory")
    public void listen14(List<Message<?>> list) {
        System.out.println(list);
    }

    @KafkaListener(id = "listMsgAck", topics = "myTopic", containerFactory = "batchFactory")
    public void listen15(List<Message<?>> list, Acknowledgment ack) {
        System.out.println(list);
    }

    @KafkaListener(id = "listMsgAckConsumer", topics = "myTopic", containerFactory = "batchFactory")
    public void listen16(List<Message<?>> list, Acknowledgment ack, Consumer<?, ?> consumer) {
        System.out.println(list);
    }

    @KafkaListener(id = "pollResults", topics = "myTopic", containerFactory = "batchFactory")
    public void pollResults(ConsumerRecords<?, ?> records) {
        System.out.println(records);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
