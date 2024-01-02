package ru.gsk16.readings.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import ru.gsk16.readings.TestKafkaConsumerFirst;
import ru.gsk16.readings.TestKafkaConsumerSecond;
import ru.gsk16.readings.enums.Template;
import ru.gsk16.readings.model.InfoMessage;
import ru.gsk16.readings.service.impl.KafkaSenderImpl;

import javax.validation.ConstraintViolationException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {
        KafkaAutoConfiguration.class,
        TestKafkaConsumerFirst.class,
        TestKafkaConsumerSecond.class,
        KafkaSenderImpl.class,
        ValidationAutoConfiguration.class
})
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@TestPropertySource(properties = {
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.kafka.consumer.group-id=test-group"
})
class EmbeddedKafkaTest {
    @Autowired
    private TestKafkaConsumerFirst consumer;
    @Autowired
    private TestKafkaConsumerSecond consumer2;
    @Autowired
    private KafkaSender kafkaSender;
    @Value("${application.kafka.test.topic-first}")
    private String topicFirst;
    @Value("${application.kafka.test.topic-second}")
    private String topicSecond;

    @Test
    public void whenMsgSentWithTopicAndPayloadThenSendNotificationReceived() throws Exception {
        InfoMessage msg = new InfoMessage().setTemplate(Template.START_OF_TRANSFER_PERIOD);
        kafkaSender.sendNotification(topicFirst, msg);
        consumer.getLatch().await(10_000, TimeUnit.MILLISECONDS);
        assertThat(consumer.getLatch().getCount()).isEqualTo(0);
        assertThat(consumer.getConsumerRecord().topic()).isEqualTo(topicFirst);
        assertThat(consumer.getConsumerRecord().value()).isEqualTo(msg);
    }

    @Test
    public void whenMsgSentWithTopicAndKeyAndPayloadThenSendNotificationReceived() throws Exception {
        InfoMessage msg = new InfoMessage().setBoxId(1).setTemplate(Template.START_OF_TRANSFER_PERIOD);
        kafkaSender.sendNotification(topicSecond, msg.getBoxId().toString(), msg);
        consumer2.getLatch().await(10_000, TimeUnit.MILLISECONDS);
        assertThat(consumer2.getLatch().getCount()).isEqualTo(0);
        assertThat(consumer2.getConsumerRecord().topic()).isEqualTo(topicSecond);
        assertThat(consumer2.getConsumerRecord().value()).isEqualTo(msg);
    }

    @Test
    public void whenMsgSentWithNotValidMsgThenSendNotificationEx() {
        assertThrows(ConstraintViolationException.class,
                () -> kafkaSender.sendNotification(topicFirst, "key", new InfoMessage()));
    }

    @Test
    public void whenMsgSentWithNotValidMsgThenSendNotificationExToo() {
        assertThrows(ConstraintViolationException.class,
                () -> kafkaSender.sendNotification(topicFirst, new InfoMessage()));
    }
}
