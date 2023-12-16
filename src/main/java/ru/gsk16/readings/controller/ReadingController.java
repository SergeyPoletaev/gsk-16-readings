package ru.gsk16.readings.controller;

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
public class ReadingController {
    private final ReadingService readingService;

    @PostMapping("/send")
    public ResponseEntity<Void> send(@RequestBody @Valid ReadingDto readingDto) {
        log.info("---> Передача показаний за гараж №{}", readingDto.getBoxId());
        return readingService.send(readingDto)
                ? ResponseEntity.ok().build() : ResponseEntity.internalServerError().build();
    }

    @GetMapping("/getStatistic")
    public ResponseEntity<Page<StatisticDto>> getStatistic(@RequestParam(value = "pageNum", defaultValue = "0")
                                                           @Min(0) Integer pageNum,
                                                           @RequestParam(value = "pageSize", defaultValue = "10")
                                                           @Min(0) @Max(100) Integer pageSize,
                                                           @RequestParam(value = "boxId") @NotNull Integer boxId) {
        log.info("---> Запрос статистики по передаче показаний за гараж №{}", boxId);
        return ResponseEntity.ok(readingService.findAllByBoxId(boxId, PageRequest.of(pageNum, pageSize)));
    }
}