package org.tr.candlesticks.repository;

import org.tr.candlesticks.model.Instrument;

public interface InstrumentRepository {
    Instrument get(String isin);
    void update(Instrument instrument);
    void delete(Instrument instrument);
}
