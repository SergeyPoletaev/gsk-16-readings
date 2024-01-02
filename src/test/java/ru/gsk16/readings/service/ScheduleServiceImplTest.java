package ru.gsk16.readings.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.gsk16.readings.model.InfoMessage;
import ru.gsk16.readings.repository.ReadingRepository;
import ru.gsk16.readings.service.impl.ScheduleServiceImpl;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static ru.gsk16.readings.enums.Template.END_OF_TRANSFER_PERIOD;
import static ru.gsk16.readings.enums.Template.START_OF_TRANSFER_PERIOD;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceImplTest {
    @Mock
    private ReadingRepository readingRepository;
    @Mock
    private EventService eventService;
    @InjectMocks
    private ScheduleServiceImpl scheduleService;
    @Mock
    private Clock clock;
    @Captor
    private ArgumentCaptor<InfoMessage> msgCaptor;
    @Captor
    private ArgumentCaptor<String> boxIdCaptor;

    private static Stream<Arguments> providerLastDayOfMonth() {
        return Stream.of(
                Arguments.of(28, 2, 2023),
                Arguments.of(29, 2, 2024),
                Arguments.of(30, 6, 2023),
                Arguments.of(31, 7, 2023)
        );
    }

    private static Stream<Arguments> providerNotLastDayOfMonth() {
        return Stream.of(
                Arguments.of(27, 2, 2023),
                Arguments.of(29, 6, 2023),
                Arguments.of(30, 7, 2023)
        );
    }

    @Test
    void notifyStartPeriod() {
        InfoMessage msg = new InfoMessage().setTemplate(START_OF_TRANSFER_PERIOD);
        scheduleService.notifyStartPeriod();
        verify(eventService).sendGeneralNotification(msgCaptor.capture());
        assertThat(msgCaptor.getValue()).isEqualTo(msg);
    }

    @ParameterizedTest
    @MethodSource("providerLastDayOfMonth")
    void whenActionInLastDayOfMonthThenNotifyEndPeriod(int day, int month, int year) {
        List<InfoMessage> msg = List.of(
                new InfoMessage().setBoxId(1).setTemplate(END_OF_TRANSFER_PERIOD),
                new InfoMessage().setBoxId(2).setTemplate(END_OF_TRANSFER_PERIOD)
        );
        List<String> keys = List.of("1", "2");
        List<Integer> boxes = List.of(1, 2);

        LocalDate date = LocalDate.of(year, month, day);
        Clock fixedClock = Clock.fixed(date.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());

        when(readingRepository.findAllThoseNotProvideReadings(date.getMonthValue(), date.getYear())).thenReturn(boxes);

        scheduleService.notifyEndPeriod();

        verify(readingRepository).findAllThoseNotProvideReadings(anyInt(), anyInt());
        verify(eventService, times(2)).sendPersonallyNotification(boxIdCaptor.capture(), msgCaptor.capture());
        assertThat(boxIdCaptor.getAllValues()).isEqualTo(keys);
        assertThat(msgCaptor.getAllValues()).isEqualTo(msg);
    }

    @ParameterizedTest
    @MethodSource("providerNotLastDayOfMonth")
    void whenActionInNotLastDayOfMonthThenNotifyEndPeriod(int day, int month, int year) {
        LocalDate date = LocalDate.of(year, month, day);
        Clock fixedClock = Clock.fixed(date.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());

        scheduleService.notifyEndPeriod();

        verify(readingRepository, never()).findAllThoseNotProvideReadings(anyInt(), anyInt());
        verify(eventService, never()).sendPersonallyNotification(boxIdCaptor.capture(), msgCaptor.capture());
    }
}