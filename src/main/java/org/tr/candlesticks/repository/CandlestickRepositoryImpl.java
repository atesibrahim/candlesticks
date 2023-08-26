package org.tr.candlesticks.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.tr.candlesticks.model.Candlestick;
import org.tr.candlesticks.model.Quote;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

@Repository
public class CandlestickRepositoryImpl implements CandlestickRepository {

    private final Logger logger = LoggerFactory.getLogger(CandlestickRepositoryImpl.class);
    private static final Map<String, ConcurrentSkipListMap<Instant, Candlestick>> candleSticks = new ConcurrentHashMap<>(); // Store candlesticks by ISIN

    @Override
    public Map<Instant, Candlestick> get(final String isin) {
        logger.info("getCandlesticks request received for isin: {}", isin);
        return candleSticks.getOrDefault(isin, new ConcurrentSkipListMap<>());
    }

    @Override
    public void delete(final String isin) {
        candleSticks.remove(isin);
    }

    @Override
    public void update(final Quote quote) {
        if (quote == null) return;

        final Instant quoteMin = quote.getTimestamp().truncatedTo(ChronoUnit.MINUTES);

        if (!candleSticks.containsKey(quote.getIsin())) {
            candleSticks.put(quote.getIsin(), new ConcurrentSkipListMap<>(Comparator.reverseOrder()));
            candleSticks.get(quote.getIsin()).put(quoteMin, buildCandlestick(quote));
        } else {
            if (candleSticks.get(quote.getIsin()).containsKey(quoteMin)) {
                Candlestick cs = candleSticks.get(quote.getIsin()).get(quoteMin);
                cs.setClosePrice(quote.getPrice());
                cs.setCloseTimestamp(quote.getTimestamp());
                cs.setHighPrice(Math.max(cs.getHighPrice(), quote.getPrice()));
                cs.setLowPrice(Math.min(cs.getLowPrice(), quote.getPrice()));
            } else {
                candleSticks.get(quote.getIsin()).put(quoteMin, buildCandlestick(quote));
                if(candleSticks.get(quote.getIsin()).containsKey(quoteMin.minus(1, ChronoUnit.MINUTES))) {
                    Candlestick cs = candleSticks.get(quote.getIsin()).get(quoteMin.minus(1, ChronoUnit.MINUTES));
                    cs.setCloseTimestamp(quote.getTimestamp());
                }
            }
        }
    }

    private Candlestick buildCandlestick(final Quote quote) {
        return Candlestick.builder()
                .openPrice(quote.getPrice())
                .highPrice(quote.getPrice())
                .lowPrice(quote.getPrice())
                .closePrice(quote.getPrice())
                .openTimestamp(quote.getTimestamp())
                .closeTimestamp(quote.getTimestamp())
                .build();
    }
}
