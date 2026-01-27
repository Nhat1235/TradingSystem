package org.aquariux.tradingsystem.core.domain.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aquariux.tradingsystem.core.domain.asset.AssetHolding;
import org.aquariux.tradingsystem.model.BaseEntity;

import java.util.List;

@Entity
@Table(name = "wallets")
@Getter
@Setter
@NoArgsConstructor
public class WalletLedger extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private AccountProfile userAccount;

    @OneToMany(mappedBy = "wallet", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private List<AssetHolding> assetHoldings;
}
