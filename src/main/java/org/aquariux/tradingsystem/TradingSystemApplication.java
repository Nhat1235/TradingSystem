package org.aquariux.tradingsystem;

import org.aquariux.tradingsystem.core.domain.asset.AssetHolding;
import org.aquariux.tradingsystem.core.domain.asset.AssetToken;
import org.aquariux.tradingsystem.core.domain.market.MarketPair;
import org.aquariux.tradingsystem.core.domain.market.MarketVenue;
import org.aquariux.tradingsystem.core.domain.user.AccountProfile;
import org.aquariux.tradingsystem.core.domain.user.WalletLedger;
import org.aquariux.tradingsystem.core.repository.AccountProfileRepository;
import org.aquariux.tradingsystem.core.repository.AssetHoldingRepository;
import org.aquariux.tradingsystem.core.repository.AssetTokenRepository;
import org.aquariux.tradingsystem.core.repository.MarketPairRepository;
import org.aquariux.tradingsystem.core.repository.WalletLedgerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@EnableScheduling
public class TradingSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(TradingSystemApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(AssetTokenRepository assetTokenRepository,
                                   MarketPairRepository marketPairRepository,
                                   AccountProfileRepository accountProfileRepository,
                                   WalletLedgerRepository walletLedgerRepository,
                                   AssetHoldingRepository assetHoldingRepository) {
        return args -> {
            Map<String, AssetToken> assetsBySymbol = seedAssets(assetTokenRepository);
            seedMarkets(marketPairRepository, assetsBySymbol);
            seedUserWallet(accountProfileRepository, walletLedgerRepository, assetHoldingRepository, assetsBySymbol);
        };
    }

    private static Map<String, AssetToken> seedAssets(AssetTokenRepository assetTokenRepository) {
        List<AssetToken> assets = new ArrayList<>();
        assets.add(buildAsset("Bitcoin", "BTC"));
        assets.add(buildAsset("Ethereum", "ETH"));
        assets.add(buildAsset("Tether", "USDT"));
        assetTokenRepository.saveAll(assets);

        Map<String, AssetToken> assetsBySymbol = new LinkedHashMap<>();
        for (AssetToken asset : assets) {
            assetsBySymbol.put(asset.getSymbol(), asset);
        }
        return assetsBySymbol;
    }

    private static void seedMarkets(MarketPairRepository marketPairRepository,
                                    Map<String, AssetToken> assetsBySymbol) {
        List<MarketPair> markets = new ArrayList<>();
        markets.add(buildMarket("BTCUSDT", "BTC", "USDT", assetsBySymbol));
        markets.add(buildMarket("ETHUSDT", "ETH", "USDT", assetsBySymbol));
        marketPairRepository.saveAll(markets);
    }

    private static void seedUserWallet(AccountProfileRepository accountProfileRepository,
                                       WalletLedgerRepository walletLedgerRepository,
                                       AssetHoldingRepository assetHoldingRepository,
                                       Map<String, AssetToken> assetsBySymbol) {
        AccountProfile user = new AccountProfile();
        user.setName("Nhatpl");
        AccountProfile successfulUser = accountProfileRepository.save(user);

        WalletLedger wallet = new WalletLedger();
        wallet.setUserAccount(user);
        walletLedgerRepository.save(wallet);

        AssetHolding usdtHolding = new AssetHolding();
        usdtHolding.setQty(50000);
        usdtHolding.setAssetId(assetsBySymbol.get("USDT").getId());
        usdtHolding.setAccountId(successfulUser.getId());
        usdtHolding.setWallet(wallet);
        assetHoldingRepository.save(usdtHolding);
    }

    private static AssetToken buildAsset(String name, String symbol) {
        AssetToken asset = new AssetToken();
        asset.setName(name);
        asset.setSymbol(symbol);
        return asset;
    }

    private static MarketPair buildMarket(String symbol,
                                          String baseSymbol,
                                          String quoteSymbol,
                                          Map<String, AssetToken> assetsBySymbol) {
        MarketPair market = new MarketPair();
        market.setSymbol(symbol);
        market.setMarketVenue(MarketVenue.SPOT);
        market.setBaseAssetId(assetsBySymbol.get(baseSymbol).getId());
        market.setQuoteAssetId(assetsBySymbol.get(quoteSymbol).getId());
        return market;
    }
}
