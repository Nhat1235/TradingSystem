package org.aquariux.tradingsystem.wallet.service;

import org.aquariux.tradingsystem.wallet.models.response.WalletBalanceResponse;

public interface WalletService {
    WalletBalanceResponse getWalletBalance(long accountId);
}
