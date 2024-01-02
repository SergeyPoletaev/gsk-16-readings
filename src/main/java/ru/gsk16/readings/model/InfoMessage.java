package ru.gsk16.readings.model;

import lombok.Data;
import ru.gsk16.readings.enums.Template;

import javax.validation.constraints.NotBlank;

@Data
public class InfoMessage {
    private Integer boxId;
    @NotBlank
    private Template template;
}
