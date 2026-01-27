package org.aquariux.tradingsystem.core.domain.asset;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aquariux.tradingsystem.core.domain.user.WalletLedger;
import org.aquariux.tradingsystem.model.BaseEntity;

@Entity
@Table(name = "asset_holding")
@Getter
@Setter
@NoArgsConstructor
public class AssetHolding extends BaseEntity {

    private long accountId;
    private long assetId;
    private double qty;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private WalletLedger wallet;
}
