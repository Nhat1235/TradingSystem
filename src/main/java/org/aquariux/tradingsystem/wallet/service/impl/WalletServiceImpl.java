package org.aquariux.tradingsystem.wallet.service.impl;

import org.aquariux.tradingsystem.core.domain.asset.AssetHolding;
import org.aquariux.tradingsystem.core.domain.asset.AssetToken;
import org.aquariux.tradingsystem.core.repository.AssetHoldingRepository;
import org.aquariux.tradingsystem.core.repository.AssetTokenRepository;
import org.aquariux.tradingsystem.exception.ResourceNotFoundException;
import org.aquariux.tradingsystem.wallet.models.response.WalletBalanceResponse;
import org.aquariux.tradingsystem.wallet.service.WalletService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WalletServiceImpl implements WalletService {
    private final AssetHoldingRepository assetHoldingRepository;
    private final AssetTokenRepository assetTokenRepository;

    public WalletServiceImpl(AssetHoldingRepository assetHoldingRepository,
                             AssetTokenRepository assetTokenRepository) {
        this.assetHoldingRepository = assetHoldingRepository;
        this.assetTokenRepository = assetTokenRepository;
    }

    @Override
    public WalletBalanceResponse getWalletBalance(long accountId) {
        List<AssetHolding> holdings = assetHoldingRepository.findAllByAccountId(accountId);
        if (holdings.isEmpty()) {
            throw new ResourceNotFoundException("Wallet", "accountId", accountId);
        }

        Map<Long, String> symbolById = assetTokenRepository.findAllById(
                        holdings.stream().map(AssetHolding::getAssetId).distinct().collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(AssetToken::getId, AssetToken::getSymbol, (a, b) -> a, HashMap::new));

        List<WalletBalanceResponse.AssetBalance> balances = holdings.stream()
                .map(holding -> WalletBalanceResponse.AssetBalance.builder()
                        .assetId(holding.getAssetId())
                        .symbol(symbolById.getOrDefault(holding.getAssetId(), ""))
                        .quantity(holding.getQty())
                        .build())
                .collect(Collectors.toList());

        return WalletBalanceResponse.builder()
                .accountId(accountId)
                .balances(balances)
                .build();
    }
}
