package ru.gsk16.readings.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.gsk16.readings.model.entity.Reading;

import java.util.Optional;

public interface ReadingRepository extends JpaRepository<Reading, Long> {

    @Query(value = """
            SELECT id FROM reading
            WHERE  box_id = :boxId AND date_part('month', send_at) = :month AND date_part('year', send_at) = :year""",
            nativeQuery = true)
    Optional<Long> findByBoxIdInCurrentMonth(@Param("boxId") Integer boxId,
                                             @Param("month") Integer month,
                                             @Param("year") Integer year);
}