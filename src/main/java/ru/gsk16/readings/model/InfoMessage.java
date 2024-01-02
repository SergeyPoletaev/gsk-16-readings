package ru.gsk16.readings.model;

import lombok.Data;
import ru.gsk16.readings.enums.Template;

import javax.validation.constraints.NotNull;

@Data
public class InfoMessage {
    private Integer boxId;
    @NotNull
    private Template template;
}
