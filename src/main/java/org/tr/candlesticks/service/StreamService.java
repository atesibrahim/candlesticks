package org.tr.candlesticks.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tr.candlesticks.model.Candlestick;
import org.tr.candlesticks.model.Instrument;
import org.tr.candlesticks.model.Quote;
import org.tr.candlesticks.repository.CandlestickRepository;
import org.tr.candlesticks.repository.InstrumentRepository;
import org.tr.candlesticks.repository.QuoteRepository;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StreamService {

    private final InstrumentRepository instrumentRepository;
    private final QuoteRepository quoteRepository;
    private final CandlestickRepository candlestickRepository;

    private final Logger logger = LoggerFactory.getLogger(StreamService.class);

    public Map<Instant, Candlestick> getCandlesticks(String isin) {
        logger.info("getCandlesticks request received for isin: {}", isin);
        return candlestickRepository.get(isin);
    }

    public List<Quote> getQuotes(String isin) {
        return quoteRepository.getQuotes(isin);
    }

    public Instrument getInstrument(String isin) {
        return instrumentRepository.get(isin);
    }

    public void updateInstrument(Instrument instrument) {
        instrumentRepository.update(instrument);
    }

    public void deleteInstrument(Instrument instrument) {
        instrumentRepository.delete(instrument);
    }

    public void processQuoteUpdate(Quote quote) {
        logger.debug("processQuoteUpdate request received for isin: {}", quote.getIsin());
        quoteRepository.update(quote);
    }
}
