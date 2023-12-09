package ru.gsk16.readings.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gsk16.readings.mapper.ReadingMapper;
import ru.gsk16.readings.model.ReadingDto;
import ru.gsk16.readings.model.entity.Reading;
import ru.gsk16.readings.repository.ReadingRepository;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReadingServiceImpl implements ReadingService {
    private static final Integer START_DATE_SENDING = 25;

    private final ReadingRepository readingRepository;
    private final ReadingMapper readingMapper;

    @Override
    @Transactional
    public boolean send(ReadingDto readingDto) {
        log.info("Передача показаний за гараж №{}", readingDto.getBoxId());
        LocalDate currentDate = LocalDate.now();
        if (currentDate.getDayOfMonth() < START_DATE_SENDING) {
            throw new IllegalArgumentException(
                    String.format("Показания эл.счетчика гаража %s переданы %s. Передача показаний доступна с 25 числа месяца",
                            readingDto.getBoxId(), currentDate)
            );
        }
        Reading entity = readingMapper.readingFrom(readingDto);
        Optional<Long> entityId =
                readingRepository.findByBoxIdInCurrentMonth(entity.getBoxId(), currentDate.getMonthValue(), currentDate.getYear());
        entityId.ifPresent(entity::setId);
        return readingRepository.save(entity).getId() != null;
    }

}
