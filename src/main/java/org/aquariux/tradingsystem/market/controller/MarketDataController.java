package org.aquariux.tradingsystem.market.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.aquariux.tradingsystem.common.ApiResponse;
import org.aquariux.tradingsystem.common.Constants;
import org.aquariux.tradingsystem.market.models.response.LatestAggregatedPriceResponse;
import org.aquariux.tradingsystem.market.models.response.MarketTickBySideResponse;
import org.aquariux.tradingsystem.market.service.MarketDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.API_BASE_PATH + "/market-data")
@Tag(name = "Market Data", description = "Aggregated market price endpoints")
public class MarketDataController {
    private final MarketDataService marketDataService;

    public MarketDataController(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }

    /*
    THis API retrieve the best (latest) aggregated price
    */

    @GetMapping("/latest")
    @Operation(summary = "Get latest aggregated price", description = "Returns latest best bid/ask from stored snapshots")
    public ResponseEntity<ApiResponse<LatestAggregatedPriceResponse>> latestAggregatedPrice(
            @RequestParam("symbol") String symbol) {
        LatestAggregatedPriceResponse response = marketDataService.getLatestAggregatedPrice(symbol);
        return ResponseEntity.ok(ApiResponse.success("Latest aggregated price", response));
    }
}
