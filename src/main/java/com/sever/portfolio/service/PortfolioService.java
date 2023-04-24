package com.sever.portfolio.service;

import com.sever.portfolio.dto.PortfolioValuesDto;
import com.sever.portfolio.entity.Portfolio;
import com.sever.portfolio.entity.PortfolioItem;

import java.util.List;

/**
 *
 */
public interface PortfolioService {

    List<Portfolio> getAll();

    Portfolio addItem(String id, PortfolioItem dto);

    void deleteItem(String id, String itemId);

    Portfolio newPortfolio(Portfolio portfolio);

    List<Portfolio> calculatePortfolioEvaluations();

    void delete(String id);

    List<PortfolioValuesDto> getValues();
}
