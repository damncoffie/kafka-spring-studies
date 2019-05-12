package com.cooper.controllers;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

public class MyKafkaListener {

    @KafkaListener(id = "myListener", topics = "myTopic", autoStartup = "${listen.auto.start:true}",
            concurrency = "${listen.concurrency:3}")
    public void listen(String data) {
        System.out.println(data);
    }

    @KafkaListener(id = "thing2", topicPartitions =
            {@TopicPartition(topic = "topic1", partitions = {"0", "1"}),
                    @TopicPartition(topic = "topic2", partitions = "0",
                            partitionOffsets = @PartitionOffset(partition = "1", initialOffset = "100"))
            })
    public void listen(ConsumerRecord<?, ?> record) {
        System.out.println(record.key());
    }

    @KafkaListener(id = "cat", topics = "myTopic",
            containerFactory = "kafkaManualAckListenerContainerFactory")
    public void listen(String data, Acknowledgment ack) {
        System.out.println(data);
        ack.acknowledge();
    }

    @KafkaListener(id = "qux", topicPattern = "myTopic1")
    public void listen(@Payload String foo,
                       @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) Integer key,
                       @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts
    ) {
        System.out.println(foo);
    }
}
