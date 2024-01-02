package ru.gsk16.readings.configuration;

import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ru.gsk16.readings.service.impl.ScheduleServiceImpl;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@ConditionalOnProperty(name = "application.scheduling.enabled", matchIfMissing = true)
public class SchedulerConfig {
    private final ScheduleServiceImpl scheduleService;

    @Scheduled(cron = "${application.scheduling.start-transfer-period}")
    @SchedulerLock(name = "notifyStartPeriod", lockAtLeastFor = "5m")
    public void notifyStartPeriod() {
        scheduleService.notifyStartPeriod();
    }

    @Scheduled(cron = "${application.scheduling.end-transfer-period}")
    @SchedulerLock(name = "notifyEndPeriod", lockAtLeastFor = "5m")
    public void notifyEndPeriod() {
        scheduleService.notifyEndPeriod();
    }
}
