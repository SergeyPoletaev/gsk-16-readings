package ru.gsk16.readings.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
@Schema(description = "Передаваемые показания эл.счетчика гаража")
public class ReadingDto {
    @NotNull
    @Schema(description = "Показания эл.счетчика", example = "123456")
    private Integer reading;
    @NotNull
    @Schema(description = "номер гаража", example = "24")
    private Integer boxId;
}
