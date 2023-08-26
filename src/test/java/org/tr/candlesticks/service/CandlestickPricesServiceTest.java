package org.tr.candlesticks.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.tr.candlesticks.model.Candlestick;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CandlestickPricesServiceTest {

    @Mock
    private StreamService streamService;

    @InjectMocks
    private CandlestickPricesService candlestickPricesService;

    @Test
    void testGetAggregatedPriceHistory_NoCandlesticks() {
        //Given
        String isin = "ABC123";
        when(streamService.getCandlesticks(isin)).thenReturn(new HashMap<>());

        //When
        List<Candlestick> result = candlestickPricesService.getLastThirtyMinutesPrices(isin);

        //Then
        assertEquals(0, result.size());
    }

    @Test
    void testGetAggregatedPriceHistory_WithCandlesticks() {
        //Given
        String isin = "XYZ789";
        Map<Instant, Candlestick> candlesticks = new HashMap<>();
        Instant now = Instant.now();
        candlesticks.put(now.minusSeconds(200), Candlestick.builder().build());
        candlesticks.put(now.minusSeconds(100), Candlestick.builder().build());
        candlesticks.put(now.minusSeconds(20), Candlestick.builder().build());

        when(streamService.getCandlesticks(isin)).thenReturn(candlesticks);

        //When
        List<Candlestick> result = candlestickPricesService.getLastThirtyMinutesPrices(isin);

        //Then
        assertTrue( result.size() > 0);
    }
}
