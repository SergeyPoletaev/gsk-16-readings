package ru.gsk16.readings.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import ru.gsk16.readings.model.InfoMessage;
import ru.gsk16.readings.service.impl.EventServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = EventServiceImpl.class)
@TestPropertySource(properties = {
        "application.kafka.producer.topic.general=general",
        "application.kafka.producer.topic.personally=personally"
})
class EventServiceImplTest {
    @MockBean
    private KafkaSender kafkaSender;
    @Autowired
    private EventService eventService;
    @Captor
    private ArgumentCaptor<String> topicCaptor;
    @Captor
    private ArgumentCaptor<InfoMessage> msgCaptor;
    @Captor
    private ArgumentCaptor<String> keyCaptor;

    @Test
    void sendGeneralNotification() {
        String topic = "general";
        InfoMessage msg = new InfoMessage();
        eventService.sendGeneralNotification(msg);
        Mockito.verify(kafkaSender).sendNotification(topicCaptor.capture(), msgCaptor.capture());
        assertThat(topicCaptor.getValue()).isEqualTo(topic);
        assertThat(msgCaptor.getValue()).isEqualTo(msg);
    }

    @Test
    void sendPersonallyNotification() {
        String topic = "personally";
        String key = "key";
        InfoMessage msg = new InfoMessage();
        eventService.sendPersonallyNotification(key, msg);
        Mockito.verify(kafkaSender).sendNotification(topicCaptor.capture(), keyCaptor.capture(), msgCaptor.capture());
        assertThat(topicCaptor.getValue()).isEqualTo(topic);
        assertThat(keyCaptor.getValue()).isEqualTo(key);
        assertThat(msgCaptor.getValue()).isEqualTo(msg);
    }
}