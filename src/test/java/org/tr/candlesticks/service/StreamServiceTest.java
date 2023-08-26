package org.tr.candlesticks.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.tr.candlesticks.model.Candlestick;
import org.tr.candlesticks.model.Quote;
import org.tr.candlesticks.repository.CandlestickRepository;
import org.tr.candlesticks.repository.InstrumentRepository;
import org.tr.candlesticks.repository.QuoteRepository;
import org.tr.candlesticks.model.Instrument;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class StreamServiceTest {

    @InjectMocks
    private StreamService streamService;

    @Mock
    private InstrumentRepository instrumentRepository;
    @Mock
    private QuoteRepository quoteRepository;
    @Mock
    private CandlestickRepository candlestickRepository;

    private final ArgumentCaptor<Instrument> instrumentArgumentCaptor = ArgumentCaptor.forClass(Instrument.class);

    private static List<Quote> quotes;
    private static final Instant now = Instant.now();

    @BeforeAll
    static void init() {
        generateQuotesData();
    }

    @Test
    void testInstrumentUpdate_WhenInputAreGiven() {
        //Given
        Instrument instrument = new Instrument();
        instrument.setIsin("PM260435A354");
        instrument.setDescription("PM260435A354 descsc");
        when(instrumentRepository.get("PM260435A354")).thenReturn(instrument);
        doNothing().when(instrumentRepository).update(instrument);

        //When
        streamService.updateInstrument(instrument);

        //Then
        Instrument instrumentResult = streamService.getInstrument("PM260435A354");
        assertEquals(instrumentResult.getIsin(), instrument.getIsin());
        assertEquals(instrumentResult.getDescription(), instrument.getDescription());
    }

    @Test
    void testInstrumentDelete_WhenInputAreGiven() {

        Instrument instrument = new Instrument();
        instrument.setIsin("PM260435A354");
        doNothing().when(instrumentRepository).delete(instrumentArgumentCaptor.capture());

        streamService.deleteInstrument(instrument);

        verify(instrumentRepository).delete(instrumentArgumentCaptor.getValue());
    }

    @Test
    void testProcessQuoteUpdate_WhenInputAreGiven_CheckQuotes() {
        doNothing().when(quoteRepository).update(any());
        when(quoteRepository.getQuotes("PM260435A354")).thenReturn(new ArrayList<>(quotes));
        streamService.processQuoteUpdate(quotes.get(0));

        List<Quote> quoteResult = streamService.getQuotes("PM260435A354");

        assertNotNull(quoteResult);
    }

    @Test
    void testProcessQuoteUpdate_WhenInputAreNotGiven_CheckQuotes() {
        when(quoteRepository.getQuotes("")).thenReturn(null);
        List<Quote> quoteResult = streamService.getQuotes("");
        assertNull(quoteResult);
    }

    @Test
    void testProcessQuoteUpdate_WhenInputAreGiven_CheckCandlesticks() {
        Candlestick cs = Candlestick.builder().highPrice(1.0).build();
        Map<Instant, Candlestick> candlesticks = Map.of(now, cs);
        when(candlestickRepository.get("PM260435A354")).thenReturn(candlesticks);


        Map<Instant, Candlestick> candlesticksResult = streamService.getCandlesticks("PM260435A354");

        assertNotNull(candlesticksResult);
        assertEquals(candlesticksResult.get(now).getHighPrice(), 1.0);
    }

    private static void generateQuotesData() {
        Quote quote = new Quote(15.0, "PM260435A354", now.minus(150, ChronoUnit.SECONDS));
        Quote quote1 = new Quote(10.0, "RM260435A354", now.minus(140, ChronoUnit.SECONDS));
        Quote quote2 = new Quote(11.0, "SM260435A354", now.minus(130, ChronoUnit.SECONDS));
        Quote quote3 = new Quote(12.0, "KM260435A354", now.minus(120, ChronoUnit.SECONDS));
        Quote quote4 = new Quote(13.0, "LM260435A354", now.minus(110, ChronoUnit.SECONDS));
        Quote quote5 = new Quote(17.0, "PM260435A354", now.minus(120, ChronoUnit.SECONDS));
        Quote quote6 = new Quote(19.0, "RM260435A354", now.minus(90, ChronoUnit.SECONDS));
        Quote quote7 = new Quote(50.0, "SM260435A354", now.minus(80, ChronoUnit.SECONDS));
        Quote quote8 = new Quote(10.0, "SM260435A354", now.minus(70, ChronoUnit.SECONDS));
        Quote quote9 = new Quote(159.0, "KM260435A354", now.minus(60, ChronoUnit.SECONDS));
        Quote quote10 = new Quote(150.0, "PM260435A354", now.minus(90, ChronoUnit.SECONDS));
        Quote quote11 = new Quote(151.0, "PM260435A354", now.minus(60, ChronoUnit.SECONDS));
        Quote quote12 = new Quote(152.0, "RM260435A354", now.minus(30, ChronoUnit.SECONDS));
        Quote quote13 = new Quote(650.0, "PM260435A354", now.minus(45, ChronoUnit.SECONDS));
        Quote quote14 = new Quote(650.0, "PM260435A354", now.minus(20, ChronoUnit.SECONDS));
        Quote quote15 = new Quote(650.0, "PM260435A354", now);
        quotes = new ArrayList<>();
        quotes.add(quote);
        quotes.add(quote1);
        quotes.add(quote2);
        quotes.add(quote3);
        quotes.add(quote4);
        quotes.add(quote6);
        quotes.add(quote7);
        quotes.add(quote8);
        quotes.add(quote9);
        quotes.add(quote12);
        quotes.add(quote5);
        quotes.add(quote10);
        quotes.add(quote11);
        quotes.add(quote13);
        quotes.add(quote14);
        quotes.add(quote15);
    }

    public static List<Quote> getQuotes() {
        generateQuotesData();
        return quotes;
    }
}
