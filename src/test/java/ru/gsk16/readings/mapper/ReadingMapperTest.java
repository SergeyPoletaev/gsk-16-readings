package ru.gsk16.readings.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.gsk16.readings.model.ReadingDto;
import ru.gsk16.readings.model.entity.Reading;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ReadingMapperImpl.class)
class ReadingMapperTest {
    @Autowired
    private ReadingMapper readingMapper;

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

}