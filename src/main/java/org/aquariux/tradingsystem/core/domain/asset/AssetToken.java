package org.aquariux.tradingsystem.core.domain.asset;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aquariux.tradingsystem.model.BaseEntity;

@Entity
@Table(name = "assets_token")
@Getter
@Setter
@NoArgsConstructor
public class AssetToken extends BaseEntity {
    private String symbol;
    private String name;
}
