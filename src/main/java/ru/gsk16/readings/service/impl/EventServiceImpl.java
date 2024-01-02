package ru.gsk16.readings.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.gsk16.readings.model.InfoMessage;
import ru.gsk16.readings.service.EventService;
import ru.gsk16.readings.service.KafkaSender;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final KafkaSender kafkaSender;
    @Value("${application.kafka.producer.topic.general:GSK_GENERAL_INFO_CHECK}")
    private String generalInfoTopic;
    @Value("${application.kafka.producer.topic.personally:GSK_PERSONALLY_INFO_CHECK}")
    private String personallyInfoTopic;

    @Override
    public void sendGeneralNotification(InfoMessage msg) {
        kafkaSender.sendNotification(generalInfoTopic, msg);
    }

    @Override
    public void sendPersonallyNotification(String key, InfoMessage msg) {
        kafkaSender.sendNotification(personallyInfoTopic, key, msg);
    }
}
