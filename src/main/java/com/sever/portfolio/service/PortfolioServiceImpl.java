package com.sever.portfolio.service;

import com.sever.portfolio.dto.PortfolioValuesDto;
import com.sever.portfolio.entity.*;
import com.sever.portfolio.exception.AssertionUtil;
import com.sever.portfolio.exception.BaseException;
import com.sever.portfolio.external.BorsaGundemService;
import com.sever.portfolio.repository.PortfolioRepository;
import com.sever.portfolio.util.ExceptionUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final BorsaGundemService borsaGundemService;

    @Override
    public List<Portfolio> getAll() {
        return getPortfolioRepository().findAll(Sort.by(Sort.Direction.DESC, "createTime"));
    }

    @Transactional
    @Override
    public void deleteItem(String id, String itemId) {
        Optional<Portfolio> optionalPortfolio = getPortfolioRepository().findById(id);
        AssertionUtil.assertDataNotFound(optionalPortfolio);
        Portfolio portfolio = optionalPortfolio.orElseThrow();
        portfolio.getPortfolioItems().removeIf(o -> o.getId().equals(itemId));
        getPortfolioRepository().save(portfolio);
    }

    @Transactional
    @Override
    public Portfolio newPortfolio(Portfolio portfolio) {
        return getPortfolioRepository().save(portfolio);
    }

    @Transactional
    @Override
    public List<Portfolio> getEvaluation() {
        for (Portfolio portfolio : getAll()) {
            getEvaluation(portfolio);
        }

        return getAll();
    }

    private void getEvaluation(Portfolio portfolio) {
        try {
            Double portfolioCurrentValue = createPortfolioItemValues(portfolio);
            updatePortfolioValueTrend(portfolio, portfolioCurrentValue);
            createPortfolioValue(portfolio, portfolioCurrentValue);
            getPortfolioRepository().save(portfolio);
        } catch (BaseException e) {
            log.error(ExceptionUtil.convertStackTraceToString(e));
        }
    }

    private Double createPortfolioItemValues(Portfolio portfolio) {
        AssertionUtil.assertDataNotFound(portfolio.getPortfolioItems());
        Double portfolioCurrentValue = 0d;
        for (PortfolioItem portfolioItem : portfolio.getPortfolioItems()) {
            PortfolioItemValue portfolioItemValue = getBorsaGundemService().getCompanyValue(portfolioItem.getCompanyCode());
            portfolioItemValue.setPortfolioItem(portfolioItem);
            portfolioItemValue.refreshCurrentValue(portfolioItem.getAmount());
            portfolioItem.getPortfolioItemValues().add(portfolioItemValue);
            portfolioCurrentValue += portfolioItemValue.getCurrentValue();
        }
        return portfolioCurrentValue;
    }

    private void createPortfolioValue(Portfolio portfolio, Double portfolioCurrentValue) {
        PortfolioValue portfolioValue = new PortfolioValue();
        portfolioValue.setPortfolio(portfolio);
        portfolioValue.setCurrentValue(portfolioCurrentValue);
        portfolio.getPortfolioValues().add(portfolioValue);
    }

    private void updatePortfolioValueTrend(Portfolio portfolio, Double portfolioCurrentValue) {
        PortfolioValue portfolioValueLast = portfolio.getPortfolioValues().get(portfolio.getPortfolioValues().size() - 1);
        if (portfolioCurrentValue.doubleValue() != portfolioValueLast.getCurrentValue().doubleValue()) {
            UpOrDown currentTrend = portfolioCurrentValue > portfolioValueLast.getCurrentValue() ? UpOrDown.UP : UpOrDown.DOWN;
            if (portfolio.getPortfolioValueTrends().isEmpty()) {
                addNewPortfolioValueTrend(portfolio, currentTrend);
            } else {
                PortfolioValueTrend portfolioValueTrendLast = portfolio.getPortfolioValueTrends().get(portfolio.getPortfolioValueTrends().size() - 1);
                if (portfolioValueTrendLast.getTrend().equals(currentTrend)) {
                    portfolioValueTrendLast.incrementCurrentTrendInARow();
                } else {
                    addNewPortfolioValueTrend(portfolio, currentTrend);
                }
            }
        }
    }

    private void addNewPortfolioValueTrend(Portfolio portfolio, UpOrDown currentTrend) {
        PortfolioValueTrend portfolioValueTrend = new PortfolioValueTrend();
        portfolioValueTrend.setPortfolio(portfolio);
        portfolioValueTrend.setTrend(currentTrend);
        portfolioValueTrend.setTrendInARow(1);
        portfolio.getPortfolioValueTrends().add(portfolioValueTrend);
    }

    @Transactional
    @Override
    public void delete(String id) {
        getPortfolioRepository().deleteById(id);
    }

    @Override
    public List<PortfolioValuesDto> getValues() {
        List<PortfolioValuesDto> portfolioValuesDtos = new ArrayList<>();
        List<Portfolio> portfolios = getAll();
        for (Portfolio portfolio : portfolios) {
            if (!portfolio.getPortfolioValues().isEmpty()) {
                PortfolioValue portfolioValue = portfolio.getPortfolioValues().get(0);
                String trend = portfolio.getPortfolioValueTrends().stream().map(o -> String.format("%s%s", o.getTrend().name(), o.getTrendInARow())).collect(Collectors.joining(","));
                portfolioValuesDtos.add(PortfolioValuesDto.builder()
                        .name(portfolio.getName())
                        .value(portfolioValue.getCurrentValue())
                        .createTime(portfolioValue.getCreateTime())
                        .trend(trend)
                        .build());
            }
        }
        return portfolioValuesDtos;
    }

    @Transactional
    @Override
    public Portfolio addItem(String id, PortfolioItem portfolioItem) {
        Optional<Portfolio> optionalPortfolio = getPortfolioRepository().findById(id);
        AssertionUtil.assertDataNotFound(optionalPortfolio);
        Portfolio portfolio = optionalPortfolio.orElseThrow();
        portfolioItem.setPortfolio(portfolio);
        portfolio.getPortfolioItems().add(portfolioItem);
        getPortfolioRepository().save(portfolio);
        return portfolio;
    }
}
