package ru.gsk16.readings.service;

import ru.gsk16.readings.model.ReadingDto;

public interface ReadingService {

    boolean send(ReadingDto readingDto);
}
