package org.tr.candlesticks.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Instrument {
    private String isin;
    private String description;
}
