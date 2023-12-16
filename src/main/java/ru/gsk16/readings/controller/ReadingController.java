package ru.gsk16.readings.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.gsk16.readings.model.ReadingDto;
import ru.gsk16.readings.model.StatisticDto;
import ru.gsk16.readings.service.ReadingService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping("/readings/v1")
@RequiredArgsConstructor
@Validated
@Tag(name = "Показания эл.счетчиков", description = "Передача и получение переданных показаний")
public class ReadingController {
    private final ReadingService readingService;

    @Operation(summary = "Передача показаний эл.счетчика")
    @PostMapping("/send")
    public ResponseEntity<Void> send(
            @Parameter(required = true) @RequestBody @Valid ReadingDto readingDto) {
        log.info("---> Передача показаний за гараж №{}", readingDto.getBoxId());
        return readingService.send(readingDto)
                ? ResponseEntity.ok().build() : ResponseEntity.internalServerError().build();
    }

    @Operation(summary = "Получение статистики передачи показаний эл.счетчика")
    @GetMapping("/getStatistic")
    public ResponseEntity<Page<StatisticDto>> getStatistic(
            @Parameter(description = "номер страницы выборки")
            @RequestParam(value = "pageNum", defaultValue = "0") @Min(0) Integer pageNum,
            @Parameter(description = "размер страницы")
            @RequestParam(value = "pageSize", defaultValue = "10") @Min(0) @Max(100) Integer pageSize,
            @Parameter(description = "номер гаража")
            @RequestParam(value = "boxId") @NotNull Integer boxId) {
        log.info("---> Запрос статистики по передаче показаний за гараж №{}", boxId);
        return ResponseEntity.ok(readingService.findAllByBoxId(boxId, PageRequest.of(pageNum, pageSize)));
    }
}