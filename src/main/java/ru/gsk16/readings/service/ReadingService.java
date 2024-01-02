package ru.gsk16.readings.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.gsk16.readings.model.ReadingDto;
import ru.gsk16.readings.model.StatisticDto;

public interface ReadingService {

    /**
     * Сохранение переданных показаний эл.счетика
     *
     * @param readingDto модель передаваемых показаний {@link ReadingDto}
     * @return - признак успешного сохранения
     */
    boolean send(ReadingDto readingDto);

    /**
     * Поиск всех записей по переданному boxId
     *
     * @param boxId    номер гаража
     * @param pageable {@link Page} с параметрами нужной выборки
     * @return {@link Page} с результатами поиска
     */
    Page<StatisticDto> findAllByBoxId(Integer boxId, Pageable pageable);
}
