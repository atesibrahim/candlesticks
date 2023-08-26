package org.tr.candlesticks.repository;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.tr.candlesticks.model.Instrument;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class InstrumentRepositoryImplTest {

    @InjectMocks
    private InstrumentRepositoryImpl instrumentRepositoryImpl;

    @Mock
    private CandlestickRepository candlestickRepository;
    @Mock
    private QuoteRepository quoteRepository;

    @Test
    void getInstrument() {
        Instrument result = instrumentRepositoryImpl.get("sdfsdfsdf");
        assertNull(result);
    }

    @Test
    void updateInstrument() {
        Instrument instrument = new Instrument();
        instrument.setIsin("PM260435A354");
        instrument.setDescription("PM260435A354 descsc");
        instrumentRepositoryImpl.update(instrument);
    }

    @Test
    void deleteInstrument() {
        Instrument instrument = new Instrument();
        instrument.setIsin("PM260435A354");
        instrumentRepositoryImpl.delete(instrument);
    }

    @Test
    void getInstruments() {
        assertNotNull(InstrumentRepositoryImpl.getInstruments());
    }
}