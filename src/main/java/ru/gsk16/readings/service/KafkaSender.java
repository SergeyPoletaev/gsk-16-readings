package ru.gsk16.readings.service;

import org.springframework.validation.annotation.Validated;
import ru.gsk16.readings.model.InfoMessage;

import javax.validation.Valid;

@Validated
public interface KafkaSender {

    /**
     * Отправка сообщения в кафку
     *
     * @param topic топик
     * @param key   ключ
     * @param msg   сообщение {@link InfoMessage}
     */
    void sendNotification(String topic, String key, @Valid InfoMessage msg);

    /**
     * Отправка сообщения в кафку
     *
     * @param topic топик
     * @param msg   сообщение {@link InfoMessage}
     */
    void sendNotification(String topic, @Valid InfoMessage msg);
}
