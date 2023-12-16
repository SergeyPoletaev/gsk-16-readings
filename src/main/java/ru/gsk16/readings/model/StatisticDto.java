package ru.gsk16.readings.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@Schema(description = "Статистика по переданным показания эл.счетчика конкретного гаража")
public class StatisticDto {
    @Schema(description = "Переданные показания эл.счетчика гаража", example = "123456")
    private Integer reading;
    @Schema(description = "Передаваемые показания эл.счетчика гаража", example = "2023-12-03")
    private LocalDate period;

}
