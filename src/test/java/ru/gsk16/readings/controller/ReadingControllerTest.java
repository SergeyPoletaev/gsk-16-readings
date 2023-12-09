package ru.gsk16.readings.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.gsk16.readings.model.ReadingDto;
import ru.gsk16.readings.service.ReadingService;

import java.util.stream.Stream;

@WebMvcTest(controllers = ReadingController.class)
class ReadingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ReadingService readingService;

    @Test
    void thenCorrectRqThenSendReturn200() throws Exception {
        ReadingDto readingDto = ReadingDto.builder()
                .reading(123456)
                .boxId(24)
                .build();
        Mockito.when(readingService.send(Mockito.any(ReadingDto.class))).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/readings/v1/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(readingDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @ParameterizedTest
    @MethodSource("providerNonValidDataForReadingDto")
    void thenNonCorrectRqThenSendReturn4002(Integer reading, Integer boxId) throws Exception {
        ReadingDto readingDto = ReadingDto.builder()
                .reading(reading)
                .boxId(boxId)
                .build();
        Mockito.when(readingService.send(Mockito.any(ReadingDto.class))).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/readings/v1/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(readingDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    private static Stream<Arguments> providerNonValidDataForReadingDto() {
        return Stream.of(
                Arguments.of(null, 24),
                Arguments.of(123456, null)
        );
    }
}