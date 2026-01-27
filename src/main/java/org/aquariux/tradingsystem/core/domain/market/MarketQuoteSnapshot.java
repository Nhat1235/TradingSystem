package org.aquariux.tradingsystem.core.domain.market;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aquariux.tradingsystem.model.BaseEntity;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "market_snapshot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketQuoteSnapshot extends BaseEntity {
    private long marketId;
    private long baseAssetId;
    private long quoteAssetId;
    private String symbol;
    private double bestBidPrice;
    private double bidSize;
    private double bestAskPrice;
    private double askSize;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MarketVenue marketVenue;
}
