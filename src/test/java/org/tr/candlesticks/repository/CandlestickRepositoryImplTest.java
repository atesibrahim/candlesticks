package org.tr.candlesticks.repository;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.tr.candlesticks.model.Candlestick;
import org.tr.candlesticks.model.Quote;
import org.tr.candlesticks.service.StreamServiceTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class CandlestickRepositoryImplTest {

    @InjectMocks
    private CandlestickRepositoryImpl candlestickRepositoryImpl;

    @Test
    void getCandlesticks() {
        Map<Instant, Candlestick> result = candlestickRepositoryImpl.get("sadasd");
        assertEquals(result.size(), 0);
    }

    @Test
    void deleteCandlestick() {
        candlestickRepositoryImpl.delete("sadasd");
    }

    @Test
    void updateCandlestick() {
        Instant min = null;
        for(Quote quote: StreamServiceTest.getQuotes()) {
            min = quote.getTimestamp().truncatedTo(ChronoUnit.MINUTES);
            candlestickRepositoryImpl.update(quote);
        }

        Map<Instant, Candlestick> candlesticks = candlestickRepositoryImpl.get("PM260435A354");

        assertNotNull(candlesticks);
        assertEquals(candlesticks.get(min).getOpenTimestamp(), candlesticks.get(min.minus(1, ChronoUnit.MINUTES)).getCloseTimestamp());

    }
}