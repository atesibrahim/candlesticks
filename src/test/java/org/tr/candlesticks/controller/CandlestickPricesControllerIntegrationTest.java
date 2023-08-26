package org.tr.candlesticks.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.tr.candlesticks.service.CandlestickPricesService;
import org.tr.candlesticks.model.Candlestick;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(CandlestickPricesController.class)
@ActiveProfiles("test")
public class CandlestickPricesControllerIntegrationTest {

    @MockBean
    private CandlestickPricesService candlestickPricesService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetCandlesticks_Success() throws Exception {
        String isin = "ABC123";
        List<Candlestick> candlesticks = new ArrayList<>();
        Candlestick candlestick = Candlestick.builder()
                .openPrice(100.0).highPrice(110.0).lowPrice(90.0).closePrice(105.0).openTimestamp(Instant.now()).closeTimestamp(Instant.now())
                .build();
        candlesticks.add(candlestick);

        when(candlestickPricesService.getLastThirtyMinutesPrices(isin)).thenReturn(candlesticks);

        mockMvc.perform(MockMvcRequestBuilders.get("/candlesticks/prices")
                        .param("isin", isin))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].openPrice").value(100.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].highPrice").value(110.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].lowPrice").value(90.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].closePrice").value(105.0));
    }

}
