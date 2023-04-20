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
@Table(name = "PortfolioValue")
@Data
@Where(clause = "DELETED = '0'")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@JsonIgnoreProperties({"portfolio"})
@ToString(exclude = {"portfolio"})
public class PortfolioValue extends BaseEntity {

    private Double currentValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PORTFOLIO_ID")
    private Portfolio portfolio;
}
