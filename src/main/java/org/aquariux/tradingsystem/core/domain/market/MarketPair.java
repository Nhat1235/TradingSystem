package org.aquariux.tradingsystem.core.domain.market;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aquariux.tradingsystem.model.BaseEntity;

@Entity
@Table(name = "markets_pair")
@Getter
@Setter
@NoArgsConstructor
public class MarketPair extends BaseEntity {
    private long baseAssetId;
    private long quoteAssetId;
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MarketVenue marketVenue;
}
