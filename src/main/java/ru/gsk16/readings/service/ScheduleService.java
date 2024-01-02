package ru.gsk16.readings.service;

public interface ScheduleService {

    /**
     * инициирует отправку уведомлений о начале периода передачи показаний
     */
    void notifyStartPeriod();

    /**
     * инициирует отправку уведомлений об окончании периода передачи показаний
     */
    void notifyEndPeriod();
}
