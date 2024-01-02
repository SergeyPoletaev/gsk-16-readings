package ru.gsk16.readings.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.gsk16.readings.model.InfoMessage;
import ru.gsk16.readings.repository.ReadingRepository;
import ru.gsk16.readings.service.EventService;
import ru.gsk16.readings.service.ScheduleService;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static ru.gsk16.readings.enums.Template.END_OF_TRANSFER_PERIOD;
import static ru.gsk16.readings.enums.Template.START_OF_TRANSFER_PERIOD;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final ReadingRepository readingRepository;
    private final EventService eventService;
    private final Clock clock;

    public void notifyStartPeriod() {
        log.info("---> Начинаем задание на рассылку о начале периода передачи показаний");
        InfoMessage msg = new InfoMessage().setTemplate(START_OF_TRANSFER_PERIOD);
        eventService.sendGeneralNotification(msg);
    }

    public void notifyEndPeriod() {
        LocalDate date = LocalDate.now(clock);
        LocalDate last = date.with(TemporalAdjusters.lastDayOfMonth());
        if (date.getDayOfMonth() == last.getDayOfMonth()) {
            log.info("---> Начинаем задание на рассылку об окончании периода передачи показаний");
            List<Integer> boxes =
                    readingRepository.findAllThoseNotProvideReadings(date.getMonthValue(), date.getYear());
            boxes.stream()
                    .map(boxId -> new InfoMessage().setBoxId(boxId).setTemplate(END_OF_TRANSFER_PERIOD))
                    .forEach(msg -> eventService.sendPersonallyNotification(msg.getBoxId().toString(), msg));
        }
    }
}
