package ru.gsk16.readings.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gsk16.readings.mapper.ReadingMapper;
import ru.gsk16.readings.model.ReadingDto;
import ru.gsk16.readings.model.StatisticDto;
import ru.gsk16.readings.model.entity.Reading;
import ru.gsk16.readings.repository.ReadingRepository;
import ru.gsk16.readings.service.ReadingService;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReadingServiceImpl implements ReadingService {
    private final ReadingRepository readingRepository;
    private final ReadingMapper readingMapper;
    private final Clock clock;
    @Value("${application.reading.startDateSending:25}")
    private int startDateSending;

    @Override
    @Transactional
    public boolean send(ReadingDto readingDto) {
        LocalDate currentDate = LocalDate.now(clock);
        if (currentDate.getDayOfMonth() < startDateSending) {
            throw new IllegalArgumentException(
                    String.format("Показания эл.счетчика гаража %s переданы %s. Передача показаний доступна с %s числа месяца",
                            readingDto.getBoxId(), currentDate, startDateSending)
            );
        }
        Reading entity = readingMapper.readingFrom(readingDto);
        Optional<Reading> entityDb = readingRepository.findByBoxIdInCurrentMonth(
                entity.getBoxId(),
                currentDate.getMonthValue(),
                currentDate.getYear()
        );
        entityDb.ifPresent(ent -> entity.setId(ent.getId()));
        return readingRepository.save(entity).getId() != null;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<StatisticDto> findAllByBoxId(Integer boxId, Pageable pageable) {
        Page<Reading> page = readingRepository.findAllByBoxId(boxId, pageable);
        return page.map(readingMapper::statisticDtoFrom);
    }

}
