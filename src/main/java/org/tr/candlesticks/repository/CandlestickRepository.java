package org.tr.candlesticks.repository;

import org.tr.candlesticks.model.Candlestick;
import org.tr.candlesticks.model.Quote;

import java.time.Instant;
import java.util.Map;

public interface CandlestickRepository {
    Map<Instant, Candlestick> get(String isin);
    void update(Quote quote);
    void delete(String isin);
}
