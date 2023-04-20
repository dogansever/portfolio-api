package com.sever.portfolio.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;

import java.util.HashSet;
import java.util.Set;

@Entity
@DynamicUpdate
@Table(name = "PortfolioItem")
@Data
@Where(clause = "DELETED = '0'")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@JsonIgnoreProperties({"portfolio"})
@ToString(exclude = {"portfolio", "portfolioItemValues"})
public class PortfolioItem extends BaseEntity {

    private String companyCode;

    private Long amount;

    private Double buyPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PORTFOLIO_ID")
    private Portfolio portfolio;

    @OrderBy("createTime desc")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "portfolioItem")
    private Set<PortfolioItemValue> portfolioItemValues = new HashSet<>();
}
