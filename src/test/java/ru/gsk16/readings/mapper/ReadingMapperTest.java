package ru.gsk16.readings.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.gsk16.readings.model.ReadingDto;
import ru.gsk16.readings.model.StatisticDto;
import ru.gsk16.readings.model.entity.Reading;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ReadingMapperTest {
    @InjectMocks
    private ReadingMapperImpl readingMapper;

    @Test
    void readingFromReadingDto() {
        ReadingDto readingDto = ReadingDto.builder()
                .reading(123456)
                .boxId(24)
                .build();
        Reading exp = Reading.builder()
                .reading(123456)
                .boxId(24)
                .sendAt(LocalDateTime.now())
                .build();
        Reading entity = readingMapper.readingFrom(readingDto);
        assertThat(entity.getId()).isEqualTo(exp.getId());
        assertThat(entity.getReading()).isEqualTo(exp.getReading());
        assertThat(entity.getBoxId()).isEqualTo(exp.getBoxId());
        assertThat(entity.getSendAt().truncatedTo(ChronoUnit.SECONDS)).isEqualTo(exp.getSendAt().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    void statisticDtoFrom() {
        LocalDateTime localDateTime = LocalDateTime.now();
        Reading reading = Reading.builder()
                .reading(123456)
                .boxId(24)
                .sendAt(localDateTime)
                .build();
        StatisticDto expDto = StatisticDto.builder()
                .reading(123456)
                .period(localDateTime.toLocalDate())
                .build();
        StatisticDto resDto = readingMapper.statisticDtoFrom(reading);
        assertThat(resDto).isEqualTo(expDto);
    }

}