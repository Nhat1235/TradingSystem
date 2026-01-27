package org.aquariux.tradingsystem.core.domain.order;

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
import org.aquariux.tradingsystem.model.BaseEntity;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "trades")
@Getter
@Setter
@NoArgsConstructor
public class OrderHistory extends BaseEntity {

    private Long marketId;
    private double filledPrice;
    private double qty;
    private OrderSide orderSide;
    private Long accountId;

    @CreationTimestamp
    private Instant createdAtDatetime;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderBook order;
}
