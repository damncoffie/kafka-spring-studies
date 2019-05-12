package com.cooper.listeners;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.ProducerListener;

/*By default, the template is configured with a LoggingProducerListener, which logs errors and does nothing when the
send is successful.*/
public class MyListener implements ProducerListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyListener.class);

    @Override
    public void onSuccess(ProducerRecord producerRecord, RecordMetadata recordMetadata) {
        LOGGER.info("All good!");
    }

    @Override
    public void onSuccess(String topic, Integer partition, Object key, Object value, RecordMetadata recordMetadata) {
        LOGGER.info("All good!");
    }

    @Override
    public void onError(ProducerRecord producerRecord, Exception exception) {
        LOGGER.error("Error!");
    }

    @Override
    public void onError(String topic, Integer partition, Object key, Object value, Exception exception) {
        LOGGER.error("Error!");
    }
}
