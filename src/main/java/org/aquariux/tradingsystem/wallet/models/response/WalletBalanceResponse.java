package org.aquariux.tradingsystem.wallet.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class WalletBalanceResponse {
    private long accountId;
    private List<AssetBalance> balances;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class AssetBalance {
        private long assetId;
        private String symbol;
        private double quantity;
    }
}
