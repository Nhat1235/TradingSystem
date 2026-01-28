package org.aquariux.tradingsystem.core.ledger;

import org.aquariux.tradingsystem.core.domain.asset.AssetHolding;

import org.aquariux.tradingsystem.core.domain.order.OrderBook;
import org.aquariux.tradingsystem.core.domain.order.OrderHistory;
import org.aquariux.tradingsystem.core.domain.order.OrderSide;
import org.aquariux.tradingsystem.core.domain.order.OrderType;
import org.aquariux.tradingsystem.core.domain.user.WalletLedger;
import org.aquariux.tradingsystem.core.marketdata.MarketTicks;
import org.aquariux.tradingsystem.core.repository.AssetHoldingRepository;
import org.aquariux.tradingsystem.core.repository.WalletLedgerRepository;
import org.aquariux.tradingsystem.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BalanceLedgerImpl implements BalanceLedger {
    private final AssetHoldingRepository assetHoldingRepository;
    private final WalletLedgerRepository walletLedgerRepository;

    public BalanceLedgerImpl(AssetHoldingRepository assetHoldingRepository,
                             WalletLedgerRepository walletLedgerRepository) {
        this.assetHoldingRepository = assetHoldingRepository;
        this.walletLedgerRepository = walletLedgerRepository;
    }

    @Override
    public void verifyFunding(OrderBook order, MarketTicks marketTick) {
        long accountId = order.getAccountId();
        Optional<AssetHolding> assetHolding;
        if (order.getSide() == OrderSide.BUY) {
            assetHolding = assetHoldingRepository.findByAccountIdAndAssetId(accountId, marketTick.getQuoteAssetId());
            checkAssetBalanceForBuyOrder(assetHolding, marketTick, order);
        } else {
            assetHolding = assetHoldingRepository.findByAccountIdAndAssetId(accountId, marketTick.getBaseAssetId());
            checkAssetBalanceForSellOrder(assetHolding, order);
        }
    }

    private void checkAssetBalanceForBuyOrder(Optional<AssetHolding> assetHolding, MarketTicks marketTick, OrderBook order) {
        double unitPrice = order.getType() == OrderType.LIMIT ? order.getLimitPrice() : marketTick.getAskPrice();
        double totalOrderPrice = unitPrice * order.getQty();
        if (assetHolding.isEmpty() || assetHolding.get().getQty() < totalOrderPrice) {
            throw new BusinessException("Insufficient fund in wallet.");
        }
    }

    private void checkAssetBalanceForSellOrder(Optional<AssetHolding> assetHolding, OrderBook order) {
        if (assetHolding.isEmpty() || assetHolding.get().getQty() < order.getQty()) {
            throw new BusinessException("Insufficient fund in wallet.");
        }
    }

    @Override
    public void applyExecution(OrderHistory trade, MarketTicks marketTick) {
        long accountId = trade.getAccountId();
        Optional<AssetHolding> optionalBaseAssetHolding = assetHoldingRepository.findByAccountIdAndAssetId(accountId, marketTick.getBaseAssetId());
        Optional<AssetHolding> optionalQuoteAssetHolding = assetHoldingRepository.findByAccountIdAndAssetId(accountId, marketTick.getQuoteAssetId());
        if (trade.getSide() == OrderSide.BUY) {
            if (optionalBaseAssetHolding.isEmpty()) {
                createNewWalletForBaseAsset(marketTick, trade, accountId);
            } else {
                updateBaseAssetForBuyOrder(optionalBaseAssetHolding.get(), trade);
            }
            updateQuoteAssetForBuyOrder(optionalQuoteAssetHolding.get(), trade);
        } else {
            if (optionalQuoteAssetHolding.isEmpty()) {
                createNewWalletForQuoteAsset(marketTick, trade, accountId);
            } else {
                updateQuoteAssetForSellOrder(optionalQuoteAssetHolding.get(), trade);
            }
            updateBaseAssetForSellOrder(optionalBaseAssetHolding.get(), trade);
        }
    }

    private void createNewWalletForBaseAsset(MarketTicks marketTick, OrderHistory trade, long accountId) {
        WalletLedger wallet = walletLedgerRepository.findByUserAccountId(accountId);
        AssetHolding baseAssetHolding = new AssetHolding();
        baseAssetHolding.setAccountId(accountId);
        baseAssetHolding.setAssetId(marketTick.getBaseAssetId());
        baseAssetHolding.setQty(trade.getQty());
        baseAssetHolding.setWallet(wallet);
        assetHoldingRepository.save(baseAssetHolding);
    }

    private void createNewWalletForQuoteAsset(MarketTicks marketTick, OrderHistory trade, long accountId) {
        WalletLedger wallet = walletLedgerRepository.findByUserAccountId(accountId);
        AssetHolding quoteAssetHolding = new AssetHolding();
        quoteAssetHolding.setAccountId(accountId);
        quoteAssetHolding.setAssetId(marketTick.getQuoteAssetId());
        quoteAssetHolding.setQty(trade.getQty() * trade.getFilledPrice());
        quoteAssetHolding.setWallet(wallet);
        assetHoldingRepository.save(quoteAssetHolding);
    }

    private void updateBaseAssetForBuyOrder(AssetHolding baseAssetHolding, OrderHistory trade) {
        baseAssetHolding.setQty(baseAssetHolding.getQty() + trade.getQty());
        assetHoldingRepository.save(baseAssetHolding);
    }

    private void updateQuoteAssetForBuyOrder(AssetHolding quoteAssetHolding, OrderHistory trade) {
        double price = trade.getFilledPrice() * trade.getQty();
        quoteAssetHolding.setQty(quoteAssetHolding.getQty() - price);
        assetHoldingRepository.save(quoteAssetHolding);
    }

    private void updateQuoteAssetForSellOrder(AssetHolding quoteAssetHolding, OrderHistory trade) {
        double quantity = trade.getFilledPrice() * trade.getQty();
        quoteAssetHolding.setQty(quoteAssetHolding.getQty() + quantity);
        assetHoldingRepository.save(quoteAssetHolding);
    }

    private void updateBaseAssetForSellOrder(AssetHolding baseAssetHolding, OrderHistory trade) {
        baseAssetHolding.setQty(baseAssetHolding.getQty() - trade.getQty());
        assetHoldingRepository.save(baseAssetHolding);
    }
}
