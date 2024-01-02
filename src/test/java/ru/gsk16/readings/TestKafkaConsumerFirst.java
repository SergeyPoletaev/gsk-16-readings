package ru.gsk16.readings;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Slf4j
@Getter
@Component
public class TestKafkaConsumerFirst {
    private final CountDownLatch latch = new CountDownLatch(1);
    private ConsumerRecord<?, ?> consumerRecord;

    @KafkaListener(topics = "${application.kafka.test.topic-first}")
    public void receive(ConsumerRecord<?, ?> consumerRecord) {
        log.info("Получено сообщение: {}", consumerRecord.toString());
        this.consumerRecord = consumerRecord;
        latch.countDown();
    }
}
