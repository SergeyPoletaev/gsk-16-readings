package ru.gsk16.readings.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Template {
    START_OF_TRANSFER_PERIOD("Уважаемый абонент! Завтра начинается период передачи показаний счетчиков"),
    END_OF_TRANSFER_PERIOD("Уважаемый абонент! Завтра заканчивается срок передачи показаний счетчиков");

    private final String msg;
}
