package ru.gsk16.readings.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.gsk16.readings.TestKafkaConsumerFirst;
import ru.gsk16.readings.enums.Template;
import ru.gsk16.readings.model.InfoMessage;
import ru.gsk16.readings.service.impl.KafkaSenderImpl;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {
        KafkaAutoConfiguration.class,
        TestKafkaConsumerFirst.class,
        KafkaSenderImpl.class
})
@DirtiesContext
@TestPropertySource(properties = {
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.kafka.consumer.group-id=test-group"
})
@Testcontainers
class ContainersKafkaTest {
    private static final String IMAGE_VERSION = "confluentinc/cp-kafka:5.4.3";

    @Container
    static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse(IMAGE_VERSION));

    @Autowired
    private TestKafkaConsumerFirst consumer;
    @Autowired
    private KafkaSender kafkaSender;
    @Value("${application.kafka.test.topic-first}")
    private String topic;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
    }

    @Test
    public void whenMsgSentWithTopicAndPayloadThenSendNotificationReceived() throws Exception {
        InfoMessage msg = new InfoMessage().setTemplate(Template.START_OF_TRANSFER_PERIOD);
        kafkaSender.sendNotification(topic, msg);
        consumer.getLatch().await(10_000, TimeUnit.MILLISECONDS);
        assertThat(consumer.getLatch().getCount()).isEqualTo(0);
        assertThat(consumer.getConsumerRecord().topic()).isEqualTo(topic);
        assertThat(consumer.getConsumerRecord().value()).isEqualTo(msg);
    }
}
