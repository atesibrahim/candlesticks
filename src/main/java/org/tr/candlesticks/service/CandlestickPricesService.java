package org.tr.candlesticks.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tr.candlesticks.model.Candlestick;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandlestickPricesService {

    private final Logger logger = LoggerFactory.getLogger(CandlestickPricesService.class);
    private final StreamService streamService;

    public List<Candlestick> getLastThirtyMinutesPrices(String isin) {
        logger.info("getAggregatedPriceHistory request received for isin: {}", isin);
        return aggregateLastThirtyMinutesPrices(streamService.getCandlesticks(isin));
    }

    private List<Candlestick> aggregateLastThirtyMinutesPrices(Map<Instant, Candlestick> candlesticks) {
        if (candlesticks.isEmpty()) return new ArrayList<>();

        Instant thirtyMinutesAgo = Instant.now().minus(30, ChronoUnit.MINUTES);

        return candlesticks.entrySet().stream()
                .takeWhile(entry -> entry.getKey().isAfter(thirtyMinutesAgo))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
}
