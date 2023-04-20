package com.sever.portfolio.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;

import java.util.HashSet;
import java.util.Set;

@Entity
@DynamicUpdate
@Table(name = "Portfolio")
@Data
@Where(clause = "DELETED = '0'")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(exclude = {"portfolioItems", "portfolioValues"})
public class Portfolio extends BaseEntity {

    @NotBlank
    private String name;

    @OrderBy("createTime desc")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "portfolio", orphanRemoval = true)
    private Set<PortfolioItem> portfolioItems = new HashSet<>();

    @OrderBy("createTime desc")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "portfolio", orphanRemoval = true)
    private Set<PortfolioValue> portfolioValues = new HashSet<>();
}
