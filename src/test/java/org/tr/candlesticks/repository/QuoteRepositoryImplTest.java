package org.tr.candlesticks.repository;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.tr.candlesticks.model.Instrument;
import org.tr.candlesticks.model.Quote;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class QuoteRepositoryImplTest {

    @InjectMocks
    private QuoteRepositoryImpl quoteRepositoryImpl;
    @Mock
    private CandlestickRepository candlestickRepository;
    @Autowired
    private InstrumentRepository instrumentRepository;

    @Test
    void getQuotes() {
        List<Quote> res = quoteRepositoryImpl.getQuotes("jhsdf");
        assertNull(res);
    }

    @Test
    void processQuoteUpdate() {
        Instrument instrument = new Instrument();
        instrument.setIsin("PM260435A354");
        instrumentRepository.update(instrument);
        Quote quote = new Quote();
        quote.setIsin("PM260435A354");
        quote.setTimestamp(null);
        quoteRepositoryImpl.update(quote);
    }

    @Test
    void processQuoteUpdate_WhenIsinNotExists() {
        Quote quote = new Quote();
        quote.setIsin("twtwg");
        quote.setTimestamp(null);

        try {
            quoteRepositoryImpl.update(quote);
        } catch (RuntimeException ex) {
            assertEquals("Instrument with isin: twtwg does not exist", ex.getMessage());
        }
    }

    @Test
    void deleteQuotes() {
        quoteRepositoryImpl.delete("isin");
    }
}