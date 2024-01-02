package ru.gsk16.readings.service;

import ru.gsk16.readings.model.InfoMessage;

public interface KafkaSender {

    /**
     * Отправка сообщения в кафку
     *
     * @param topic топик
     * @param key   ключ
     * @param msg   сообщение {@link InfoMessage}
     */
    void sendNotification(String topic, String key, InfoMessage msg);

    /**
     * Отправка сообщения в кафку
     *
     * @param topic топик
     * @param msg   сообщение {@link InfoMessage}
     */
    void sendNotification(String topic, InfoMessage msg);
}
