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
@Table(name = "PortfolioValueTrend")
@Data
@Where(clause = "DELETED = '0'")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@JsonIgnoreProperties({"portfolio"})
@ToString(exclude = {"portfolio"})
public class PortfolioValueTrend extends BaseEntity {

    private UpOrDown trend;

    private Integer trendInARow;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PORTFOLIO_ID")
    private Portfolio portfolio;

    public void incrementCurrentTrendInARow() {
        setTrendInARow(trendInARow + 1);
    }
}
