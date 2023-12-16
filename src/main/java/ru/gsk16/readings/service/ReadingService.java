package ru.gsk16.readings.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.gsk16.readings.model.ReadingDto;
import ru.gsk16.readings.model.StatisticDto;

public interface ReadingService {

    boolean send(ReadingDto readingDto);

    Page<StatisticDto> findAllByBoxId(Integer boxId, Pageable pageable);
}
