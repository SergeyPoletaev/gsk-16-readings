package ru.gsk16.readings.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.gsk16.readings.model.ReadingDto;
import ru.gsk16.readings.model.StatisticDto;
import ru.gsk16.readings.model.entity.Reading;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReadingMapper {

    @Mapping(target = "sendAt", expression = "java(time())")
    Reading readingFrom(ReadingDto readingDto);

    default LocalDateTime time() {
        return LocalDateTime.now();
    }

    @Mapping(target = "period", expression = "java(period(reading))")
    StatisticDto statisticDtoFrom(Reading reading);

    default LocalDate period(Reading reading) {
        return reading.getSendAt().toLocalDate();
    }
}
