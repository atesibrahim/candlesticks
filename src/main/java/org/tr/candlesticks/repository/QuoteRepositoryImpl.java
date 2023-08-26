package org.tr.candlesticks.repository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.tr.candlesticks.model.Quote;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class QuoteRepositoryImpl implements QuoteRepository {

    private final CandlestickRepository candlestickRepository;
    private final Logger logger = LoggerFactory.getLogger(QuoteRepositoryImpl.class);
    private static final Map<String, List<Quote>> quotes = new ConcurrentHashMap<>(); // Store quotes by ISIN

    public List<Quote> getQuotes(String isin) {
        return quotes.get(isin);
    }

    public void update(final Quote quote) {
        logger.debug("processQuoteUpdate request received for isin: {}", quote.getIsin());
        if (checkInstrumentExists(quote.getIsin())) {
            quotes.computeIfAbsent(quote.getIsin(), k -> new ArrayList<>()).add(quote);
            candlestickRepository.update(quote);
        } else {
            logger.warn("Instrument with isin: {} does not exist", quote.getIsin());
            throw new IllegalArgumentException("Instrument with isin: " + quote.getIsin() + " does not exist");
        }
    }

    private boolean checkInstrumentExists(final String isin) {
        return InstrumentRepositoryImpl.getInstruments().containsKey(isin);
    }

    @Override
    public void delete(final String isin) {
        quotes.remove(isin);
    }
}
