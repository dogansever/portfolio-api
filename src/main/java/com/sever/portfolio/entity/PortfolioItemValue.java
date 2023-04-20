package com.sever.portfolio.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;

@Entity
@DynamicUpdate
@Table(name = "PortfolioItemValue")
@Data
@Where(clause = "DELETED = '0'")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@JsonIgnoreProperties({"portfolioItem"})
@ToString(exclude = {"portfolioItem"})
public class PortfolioItemValue extends BaseEntity {

    private Double currentPrice;

    private Double dailyPriceChangePercentage;

    private Double currentValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PORTFOLIO_ITEM_ID")
    private PortfolioItem portfolioItem;

    public void refreshCurrentValue(Long amount) {
        setCurrentValue(amount * getCurrentPrice());
    }
}
