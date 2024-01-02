package ru.gsk16.readings.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import ru.gsk16.readings.TestKafkaConsumer;
import ru.gsk16.readings.enums.Template;
import ru.gsk16.readings.model.InfoMessage;
import ru.gsk16.readings.service.impl.KafkaSenderImpl;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {
        KafkaAutoConfiguration.class,
        TestKafkaConsumer.class,
        KafkaSenderImpl.class
})
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@TestPropertySource(properties = {
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.kafka.consumer.group-id=test-group"
})
class EmbeddedKafkaTest {
    @Autowired
    private TestKafkaConsumer consumer;
    @Autowired
    private KafkaSender kafkaSender;
    @Value("${application.kafka.test.topic}")
    private String topic;

    @Test
    public void whenMsgSentViaSendNotificationThenMsgReceived() throws Exception {
        InfoMessage msg = new InfoMessage().setTemplate(Template.START_OF_TRANSFER_PERIOD);
        kafkaSender.sendNotification(topic, msg);
        consumer.getLatch().await(10_000, TimeUnit.MILLISECONDS);
        assertThat(consumer.getLatch().getCount()).isEqualTo(0);
        assertThat(consumer.getConsumerRecord().topic()).isEqualTo(topic);
        assertThat(consumer.getConsumerRecord().value()).isEqualTo(msg);
    }
}
