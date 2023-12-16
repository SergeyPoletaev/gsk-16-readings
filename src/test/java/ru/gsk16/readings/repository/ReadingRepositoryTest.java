package ru.gsk16.readings.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.gsk16.readings.model.entity.Reading;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class ReadingRepositoryTest {
    private static final String IMAGE_VERSION = "postgres:14-alpine";

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(IMAGE_VERSION);

    @Autowired
    private ReadingRepository readingRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @BeforeEach
    void setUp() {
        readingRepository.deleteAll();
    }

    @Test
    void findByBoxIdInCurrentMonth() {
        LocalDateTime localDateTime = LocalDateTime.now();
        Reading entity = Reading.builder()
                .boxId(24)
                .reading(123456)
                .sendAt(localDateTime)
                .build();
        readingRepository.save(entity);
        Optional<Reading> rsl =
                readingRepository.findByBoxIdInCurrentMonth(entity.getBoxId(), localDateTime.getMonthValue(), localDateTime.getYear());
        rsl.ifPresent(id -> assertThat(id).isNotNull());
    }
}