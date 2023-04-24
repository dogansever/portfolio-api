package com.sever.portfolio.service;

import com.sever.portfolio.dto.PortfolioValuesDto;
import com.sever.portfolio.entity.*;
import com.sever.portfolio.exception.AssertionUtil;
import com.sever.portfolio.exception.BaseException;
import com.sever.portfolio.external.BorsaGundemService;
import com.sever.portfolio.repository.CompanyValueRepository;
import com.sever.portfolio.repository.PortfolioRepository;
import com.sever.portfolio.util.ExceptionUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
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
    private final CompanyValueRepository companyValueRepository;
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
    public List<Portfolio> calculatePortfolioEvaluations() {
        getAll().stream().forEach(this::calculatePortfolioEvaluation);
        deleteAllCompanyValues();
        return getAll();
    }

    private void deleteAllCompanyValues() {
        List<CompanyValue> companyValues = getCompanyValueRepository().findAll();
        companyValues.stream().forEach(BaseEntity::markDeleted);
        getCompanyValueRepository().saveAll(companyValues);
    }

    private void calculatePortfolioEvaluation(Portfolio portfolio) {
        try {
            Double portfolioCurrentValue = createPortfolioItemValues(portfolio);
            createOrUpdatePortfolioValueTrend(portfolio, portfolioCurrentValue);
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
            CompanyValue companyValue = createCompanyValue(portfolioItem);

            PortfolioItemValue portfolioItemValue = new PortfolioItemValue();
            portfolioItemValue.setPortfolioItem(portfolioItem);
            portfolioItemValue.setCurrentPrice(companyValue.getCurrentPrice());
            portfolioItemValue.setDailyPriceChangePercentage(companyValue.getDailyPriceChangePercentage());
            portfolioItemValue.refreshCurrentValue();
            portfolioItem.getPortfolioItemValues().add(portfolioItemValue);
            portfolioCurrentValue += portfolioItemValue.getCurrentValue();
        }
        return portfolioCurrentValue;
    }

    private CompanyValue createCompanyValue(PortfolioItem portfolioItem) {
        CompanyValue companyValueExample = new CompanyValue();
        companyValueExample.setCompanyCode(portfolioItem.getCompanyCode());
        Optional<CompanyValue> optionalCompanyValue = getCompanyValueRepository().findOne(Example.of(companyValueExample));

        CompanyValue companyValue;
        if (optionalCompanyValue.isPresent()) {
            companyValue = optionalCompanyValue.get();
        } else {
            companyValue = getBorsaGundemService().getCompanyValue(portfolioItem.getCompanyCode());
            getCompanyValueRepository().save(companyValue);
        }
        return companyValue;
    }

    private void createPortfolioValue(Portfolio portfolio, Double portfolioCurrentValue) {
        PortfolioValue portfolioValue = new PortfolioValue();
        portfolioValue.setPortfolio(portfolio);
        portfolioValue.setCurrentValue(portfolioCurrentValue);
        portfolio.getPortfolioValues().add(portfolioValue);
    }

    private void createOrUpdatePortfolioValueTrend(Portfolio portfolio, Double portfolioCurrentValue) {
        PortfolioValue portfolioValueLatest = portfolio.getPortfolioValues().get(0);
        if (portfolioCurrentValue.doubleValue() != portfolioValueLatest.getCurrentValue().doubleValue()) {
            UpOrDown currentTrend = portfolioCurrentValue > portfolioValueLatest.getCurrentValue() ? UpOrDown.UP : UpOrDown.DOWN;
            if (portfolio.getPortfolioValueTrends().isEmpty()) {
                createPortfolioValueTrend(portfolio, currentTrend);
            } else {
                PortfolioValueTrend portfolioValueTrendLatest = portfolio.getPortfolioValueTrends().get(0);
                if (portfolioValueTrendLatest.getTrend().equals(currentTrend)) {
                    portfolioValueTrendLatest.incrementCurrentTrendInARow();
                    log.info("PortfolioValueTrend for {} is {} {}", portfolio.getName(), portfolioValueTrendLatest.getTrend(), portfolioValueTrendLatest.getTrendInARow());
                } else {
                    createPortfolioValueTrend(portfolio, currentTrend);
                }
            }
        }
    }

    private void createPortfolioValueTrend(Portfolio portfolio, UpOrDown currentTrend) {
        PortfolioValueTrend portfolioValueTrend = new PortfolioValueTrend();
        portfolioValueTrend.setPortfolio(portfolio);
        portfolioValueTrend.setTrend(currentTrend);
        portfolioValueTrend.setTrendInARow(1);
        portfolio.getPortfolioValueTrends().add(portfolioValueTrend);
        log.info("PortfolioValueTrend for {} is {} {}", portfolio.getName(), portfolioValueTrend.getTrend(), portfolioValueTrend.getTrendInARow());

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
