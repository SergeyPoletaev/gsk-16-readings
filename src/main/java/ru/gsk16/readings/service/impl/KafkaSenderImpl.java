package ru.gsk16.readings.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.gsk16.readings.model.InfoMessage;
import ru.gsk16.readings.service.KafkaSender;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class KafkaSenderImpl implements KafkaSender {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void sendNotification(String topic, String key, @Valid InfoMessage msg) {
        log.info("---> Отправка уведомления: [key {}, value {}]", key, msg);
        CompletableFuture<SendResult<String, Object>> cf = kafkaTemplate.send(topic, key, msg).completable();
        handle(cf, key, msg);
    }

    @Override
    public void sendNotification(String topic, @Valid InfoMessage msg) {
        log.info("---> Отправка уведомления: [value {}]", msg);
        CompletableFuture<SendResult<String, Object>> cf = kafkaTemplate.send(topic, msg).completable();
        handle(cf, null, msg);
    }

    private void handle(CompletableFuture<SendResult<String, Object>> cf, String key, InfoMessage msg) {
        cf.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("---> Сообщение успешно отправлено: [ key {}, value {} c офсетом {} ]",
                        key, msg, result.getRecordMetadata().offset());
            } else {
                log.error("---> Ошибка отправки сообщения: [ key {}, value {} из-за {} ]",
                        key, msg, ex.getMessage());
            }
        });
    }
}
