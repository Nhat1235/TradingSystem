package org.aquariux.tradingsystem.core.domain.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aquariux.tradingsystem.model.BaseEntity;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "order_history")
@Getter
@Setter
@NoArgsConstructor
public class OrderHistory extends BaseEntity {

    private Long marketId;
    private double filledPrice;
    private double qty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderSide side;

    private Long accountId;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderBook order;
}
