package ru.gsk16.readings.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class ReadingDto {
    @NotNull
    private Integer reading;
    @NotNull
    private Integer boxId;
}
