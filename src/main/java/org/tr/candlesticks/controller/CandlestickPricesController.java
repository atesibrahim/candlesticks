package org.tr.candlesticks.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tr.candlesticks.model.Candlestick;
import org.tr.candlesticks.service.CandlestickPricesService;

import java.util.List;

@RestController
@RequestMapping("/candlesticks")
@RequiredArgsConstructor
public class CandlestickPricesController {

    private final Logger logger = LoggerFactory.getLogger(CandlestickPricesController.class);
    private final CandlestickPricesService candlestickPricesService;

    @Operation(summary = "Returns the aggregated price history for the given isin")
    @GetMapping("/prices")
    public ResponseEntity<List<Candlestick>> list(@RequestParam String isin) {
        logger.info("/prices endpoint request received for isin: {}", isin);
        return ResponseEntity.ok(candlestickPricesService.getLastThirtyMinutesPrices(isin));
    }
}
