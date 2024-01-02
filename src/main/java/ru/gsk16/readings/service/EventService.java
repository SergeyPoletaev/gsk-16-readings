package ru.gsk16.readings.service;

import ru.gsk16.readings.model.InfoMessage;

public interface EventService {

    /**
     * Отправка уведомления общего характера для всех пользователей
     *
     * @param msg сообщение {@link InfoMessage}
     */
    void sendGeneralNotification(InfoMessage msg);

    /**
     * Отправка персонального уведомления
     *
     * @param key ключ
     * @param msg сообщение {@link InfoMessage}
     */
    void sendPersonallyNotification(String key, InfoMessage msg);
}
