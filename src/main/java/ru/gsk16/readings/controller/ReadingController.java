package ru.gsk16.readings.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gsk16.readings.model.ReadingDto;
import ru.gsk16.readings.service.ReadingService;

import javax.validation.Valid;

@RestController
@RequestMapping("/readings/v1")
@RequiredArgsConstructor
public class ReadingController {
    private  final ReadingService readingService;

    @PostMapping("/send")
    public ResponseEntity<Void> send(@RequestBody @Valid ReadingDto readingDto) {
        return readingService.send(readingDto)
                ? ResponseEntity.ok().build() : ResponseEntity.internalServerError().build();
    }
}