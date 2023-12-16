package ru.gsk16.readings.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.gsk16.readings.model.ReadingDto;
import ru.gsk16.readings.model.StatisticDto;
import ru.gsk16.readings.service.ReadingService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        when(readingService.send(any(ReadingDto.class))).thenReturn(true);
        mockMvc.perform(post("/readings/v1/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(readingDto)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource("providerNonValidDataForReadingDto")
    void thenNonCorrectRqThenSendReturn400(Integer reading, Integer boxId) throws Exception {
        ReadingDto readingDto = ReadingDto.builder()
                .reading(reading)
                .boxId(boxId)
                .build();
        when(readingService.send(any(ReadingDto.class))).thenReturn(true);
        mockMvc.perform(post("/readings/v1/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(readingDto)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void whenValidParamsThenGetStatisticReturnOk() throws Exception {
        Integer pageNum = 0;
        Integer pageSize = 2;
        Integer boxId = 10;
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<StatisticDto> page =
                new PageImpl<>(List.of(
                        StatisticDto.builder()
                                .reading(123456)
                                .period(LocalDate.of(2023, 12, 1))
                                .build())
                );
        when(readingService.findAllByBoxId(boxId, pageable)).thenReturn(page);
        MvcResult mvcResult = mockMvc.perform(get("/readings/v1/getStatistic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("pageNum", String.valueOf(pageNum))
                        .param("pageSize", String.valueOf(pageSize))
                        .param("boxId", String.valueOf(boxId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        verify(readingService).findAllByBoxId(boxId, pageable);
        assertThat(actualResponseBody).isEqualTo(objectMapper.writeValueAsString(page));
    }

    @Test
    void whenValidParamBoxIdAndDefaultOtherParamsThenGetStatisticReturnOk() throws Exception {
        int defaultPageNum = 0;
        int defaultPageSize = 10;
        Integer boxId = 24;
        Pageable pageable = PageRequest.of(defaultPageNum, defaultPageSize);
        Page<StatisticDto> page =
                new PageImpl<>(List.of(
                        StatisticDto.builder()
                                .reading(123456)
                                .period(LocalDate.of(2023, 12, 1))
                                .build())
                );
        when(readingService.findAllByBoxId(boxId, pageable)).thenReturn(page);
        MvcResult mvcResult = mockMvc.perform(get("/readings/v1/getStatistic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("boxId", String.valueOf(boxId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        verify(readingService).findAllByBoxId(boxId, pageable);
        assertThat(actualResponseBody).isEqualTo(objectMapper.writeValueAsString(page));
    }

    @Test
    void whenNonValidParamBoxIdAndDefaultOtherParamsThenGetStatisticReturn400() throws Exception {
        int defaultPageNum = 0;
        int defaultPageSize = 10;
        Integer boxId = 24;
        Pageable pageable = PageRequest.of(defaultPageNum, defaultPageSize);
        Page<StatisticDto> page =
                new PageImpl<>(List.of(
                        StatisticDto.builder()
                                .reading(123456)
                                .period(LocalDate.of(2023, 12, 1))
                                .build())
                );
        when(readingService.findAllByBoxId(boxId, pageable)).thenReturn(page);
        mockMvc.perform(get("/readings/v1/getStatistic")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
        verify(readingService, never()).findAllByBoxId(boxId, pageable);
    }

    private static Stream<Arguments> providerNonValidDataForReadingDto() {
        return Stream.of(
                Arguments.of(null, 24),
                Arguments.of(123456, null)
        );
    }
}