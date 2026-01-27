package org.aquariux.tradingsystem.core.marketdata.huobi;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HuobiTickerResponse {
    private List<HuobiTicker> data;
}

