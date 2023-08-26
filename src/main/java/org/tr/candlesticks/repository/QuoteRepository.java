package org.tr.candlesticks.repository;

import org.tr.candlesticks.model.Quote;

import java.util.List;

public interface QuoteRepository {
    List<Quote> getQuotes(String isin);
    void update(Quote quote);
    void delete(String isin);
}
