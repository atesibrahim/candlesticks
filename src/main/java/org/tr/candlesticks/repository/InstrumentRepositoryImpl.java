package org.tr.candlesticks.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.tr.candlesticks.model.Instrument;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class InstrumentRepositoryImpl implements InstrumentRepository {

    private final CandlestickRepository candlestickRepository;
    private final QuoteRepository quoteRepository;
    private static final Map<String, Instrument> instruments = new ConcurrentHashMap<>(); // Store instruments by ISIN

    public Instrument get(String isin) {
        return instruments.get(isin);
    }

    public void update(Instrument instrument) {
        instruments.put(instrument.getIsin(), instrument);
    }

    public void delete(Instrument instrument) {
        instruments.remove(instrument.getIsin(), instrument);
        quoteRepository.delete(instrument.getIsin());
        candlestickRepository.delete(instrument.getIsin());
    }

    public static Map<String, Instrument> getInstruments() {
        return instruments;
    }
}
