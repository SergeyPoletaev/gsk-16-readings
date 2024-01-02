package ru.gsk16.readings.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.gsk16.readings.model.entity.Reading;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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

    private static Stream<Arguments> providerForEmptyRsl() {
        return Stream.of(
                Arguments.of(2023, 10, 26),
                Arguments.of(2023, 10, 13)
        );
    }

    private static Stream<Arguments> providerForNotEmptyRsl() {
        return Stream.of(
                Arguments.of(2023, 12, 1),
                Arguments.of(2024, 11, 26)
        );
    }

    @Test
    void whenFindByBoxIdInCurrentMonthThenRslNotNull() {
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

    @Test
    void whenFindByBoxIdInCurrentMonthThenRslEmpty() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 10, 26, 10, 0);
        Reading entity = Reading.builder()
                .boxId(24)
                .reading(123456)
                .sendAt(localDateTime)
                .build();
        LocalDate currDate = LocalDate.of(2023, 11, 26);
        readingRepository.save(entity);
        Optional<Reading> rsl =
                readingRepository.findByBoxIdInCurrentMonth(entity.getBoxId(), currDate.getMonthValue(), currDate.getYear());
        assertThat(rsl).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("providerForEmptyRsl")
    void whenFindAllThoseNotProvideReadingsThenRslEmpty(int currYear, int currMonth, int currDay) {
        LocalDateTime date = LocalDateTime.of(2023, 10, 1, 10, 0);
        Reading entity = Reading.builder()
                .boxId(24)
                .reading(123456)
                .sendAt(date)
                .build();
        readingRepository.save(entity);
        LocalDate currentDate = LocalDate.of(currYear, currMonth, currDay);
        List<Integer> boxes =
                readingRepository.findAllThoseNotProvideReadings(currentDate.getMonthValue(), currentDate.getYear());
        assertThat(boxes).isEqualTo(Collections.emptyList());
    }

    @ParameterizedTest
    @MethodSource("providerForNotEmptyRsl")
    void whenFindAllThoseNotProvideReadingsThenRslNotEmpty(int currYear, int currMonth, int currDay) {
        LocalDateTime date = LocalDateTime.of(2023, 10, 1, 10, 0);
        Reading entity = Reading.builder()
                .boxId(24)
                .reading(123456)
                .sendAt(date)
                .build();
        readingRepository.save(entity);
        LocalDate currentDate = LocalDate.of(currYear, currMonth, currDay);
        List<Integer> boxes =
                readingRepository.findAllThoseNotProvideReadings(currentDate.getMonthValue(), currentDate.getYear());
        assertThat(boxes).isEqualTo(List.of(entity.getBoxId()));
    }
}