package ru.gsk16.readings.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.gsk16.readings.mapper.ReadingMapper;
import ru.gsk16.readings.model.ReadingDto;
import ru.gsk16.readings.model.StatisticDto;
import ru.gsk16.readings.model.entity.Reading;
import ru.gsk16.readings.repository.ReadingRepository;
import ru.gsk16.readings.service.impl.ReadingServiceImpl;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = ReadingServiceImpl.class)
class ReadingServiceImplTest {
    @MockBean
    private ReadingRepository readingRepository;
    @MockBean
    private ReadingMapper readingMapper;
    @Autowired
    private ReadingService readingService;
    @MockBean
    private Clock clock;
    @Captor
    private ArgumentCaptor<Reading> argCaptor;

    private static Stream<Arguments> providerValidDateSendReadings() {
        return Stream.of(
                Arguments.of(25),
                Arguments.of(31)
        );
    }

    @ParameterizedTest
    @MethodSource("providerValidDateSendReadings")
    void whenOnTimeSendThenReadingsSave(int dayOfMonth) {
        ReadingDto readingDto = ReadingDto.builder()
                .boxId(10)
                .reading(123456)
                .build();
        Reading entity = Reading.builder()
                .boxId(readingDto.getBoxId())
                .reading(readingDto.getReading())
                .build();

        LocalDate localDate = LocalDate.of(2023, 1, dayOfMonth);
        Clock fixedClock = Clock.fixed(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());

        when(readingMapper.readingFrom(readingDto)).thenReturn(entity);
        when(readingRepository.findByBoxIdInCurrentMonth(anyInt(), anyInt(), anyInt())).thenReturn(Optional.empty());
        when(readingRepository.save(entity)).thenReturn(new Reading().setId(1L));

        boolean rsl = readingService.send(readingDto);

        verify(readingMapper).readingFrom(readingDto);
        verify(readingRepository).findByBoxIdInCurrentMonth(anyInt(), anyInt(), anyInt());
        verify(readingRepository).save(argCaptor.capture());

        assertThat(rsl).isTrue();
        assertThat(argCaptor.getValue().getId()).isNull();
    }

    @ParameterizedTest
    @MethodSource("providerValidDateSendReadings")
    void whenOnTimeSendThenReadingsUpdated(int dayOfMonth) {
        ReadingDto readingDto = ReadingDto.builder()
                .boxId(10)
                .reading(123456)
                .build();
        Reading entity = Reading.builder()
                .boxId(readingDto.getBoxId())
                .reading(readingDto.getReading())
                .build();

        LocalDate localDate = LocalDate.of(2023, 1, dayOfMonth);
        Clock fixedClock = Clock.fixed(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());

        when(readingMapper.readingFrom(readingDto)).thenReturn(entity);
        when(readingRepository.findByBoxIdInCurrentMonth(anyInt(), anyInt(), anyInt())).thenReturn(Optional.of(new Reading().setId(1L)));
        when(readingRepository.save(entity)).thenReturn(new Reading().setId(1L));

        boolean rsl = readingService.send(readingDto);

        verify(readingMapper).readingFrom(readingDto);
        verify(readingRepository).findByBoxIdInCurrentMonth(anyInt(), anyInt(), anyInt());
        verify(readingRepository).save(argCaptor.capture());

        assertThat(rsl).isTrue();
        assertThat(1L).isEqualTo(argCaptor.getValue().getId());
    }

    @Test
    void whenEarlySendThenEx() {
        ReadingDto readingDto = ReadingDto.builder()
                .boxId(10)
                .reading(123456)
                .build();

        LocalDate localDate = LocalDate.of(2023, 1, 24);
        Clock fixedClock = Clock.fixed(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());

        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> readingService.send(readingDto));

        assertThat(thrown.getMessage())
                .isEqualTo(format(
                        "Показания эл.счетчика гаража %s переданы %s. Передача показаний доступна с 25 числа месяца",
                        readingDto.getBoxId(),
                        localDate
                ));

        verify(readingMapper, never()).readingFrom(readingDto);
        verify(readingRepository, never()).findByBoxIdInCurrentMonth(anyInt(), anyInt(), anyInt());
        verify(readingRepository, never()).save(argCaptor.capture());
    }

    @Test
    void findAllByBoxId() {
        int defaultPageNum = 0;
        int defaultPageSize = 10;
        Integer boxId = 24;
        StatisticDto statisticDto = StatisticDto.builder()
                .reading(123456)
                .period(LocalDate.of(2023, 12, 1))
                .build();
        Reading reading = Reading.builder()
                .reading(123456)
                .sendAt(LocalDateTime.of(2023, 12, 1, 0, 0))
                .build();
        Pageable pageable = PageRequest.of(defaultPageNum, defaultPageSize);
        Page<Reading> pageEntity = new PageImpl<>(List.of(reading));
        Page<StatisticDto> pageDto = new PageImpl<>(List.of(statisticDto));

        when(readingRepository.findAllByBoxId(boxId, pageable)).thenReturn(pageEntity);
        when(readingMapper.statisticDtoFrom(reading)).thenReturn(statisticDto);

        Page<StatisticDto> pageRsl = readingService.findAllByBoxId(boxId, pageable);

        verify(readingRepository).findAllByBoxId(boxId, pageable);
        verify(readingMapper).statisticDtoFrom(argCaptor.capture());
        assertThat(argCaptor.getValue().getReading()).isEqualTo(123456);
        assertThat(pageRsl).isEqualTo(pageDto);
    }
}