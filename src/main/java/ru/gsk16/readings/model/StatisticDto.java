package ru.gsk16.readings.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class StatisticDto {
    private Integer reading;
    private LocalDate period;

}
