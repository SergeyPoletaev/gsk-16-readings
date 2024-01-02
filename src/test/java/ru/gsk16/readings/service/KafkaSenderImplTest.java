package ru.gsk16.readings.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import ru.gsk16.readings.enums.Template;
import ru.gsk16.readings.model.InfoMessage;
import ru.gsk16.readings.service.impl.KafkaSenderImpl;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaSenderImplTest {
    @Mock
    private KafkaTemplate<String, InfoMessage> kafkaTemplate;
    @Mock
    private CompletableFuture<SendResult<String, InfoMessage>> cf;
    @Mock
    private ListenableFuture<SendResult<String, InfoMessage>> lf;
    @InjectMocks
    private KafkaSenderImpl kafkaProducer;

    @Test
    void whenSendNotificationWithTopicAndKeyAndMsg() {
        String topic = "topic";
        String key = "key";
        InfoMessage msg = new InfoMessage().setBoxId(1).setTemplate(Template.START_OF_TRANSFER_PERIOD);
        when(kafkaTemplate.send(topic, key, msg)).thenReturn(lf);
        when(lf.completable()).thenReturn(cf);

        kafkaProducer.sendNotification(topic, key, msg);

        verify(kafkaTemplate).send(topic, key, msg);
        verify(lf).completable();
        verify(cf).whenComplete(any(BiConsumer.class));
    }

    @Test
    void whenSendNotificationWithTopicAndMsg() {
        String topic = "topic";
        InfoMessage msg = new InfoMessage().setBoxId(1).setTemplate(Template.START_OF_TRANSFER_PERIOD);
        when(kafkaTemplate.send(topic, msg)).thenReturn(lf);
        when(lf.completable()).thenReturn(cf);

        kafkaProducer.sendNotification(topic, msg);

        verify(kafkaTemplate).send(topic, msg);
        verify(lf).completable();
        verify(cf).whenComplete(any(BiConsumer.class));
    }
}