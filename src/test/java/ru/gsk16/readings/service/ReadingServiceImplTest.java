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
import ru.gsk16.readings.mapper.ReadingMapper;
import ru.gsk16.readings.model.ReadingDto;
import ru.gsk16.readings.model.entity.Reading;
import ru.gsk16.readings.repository.ReadingRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
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
    private ReadingServiceImpl readingService;
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
}