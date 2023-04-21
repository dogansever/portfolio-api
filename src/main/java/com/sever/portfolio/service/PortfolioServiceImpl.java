package com.sever.portfolio.service;

import com.sever.portfolio.dto.PortfolioValuesDto;
import com.sever.portfolio.entity.Portfolio;
import com.sever.portfolio.entity.PortfolioItem;
import com.sever.portfolio.entity.PortfolioItemValue;
import com.sever.portfolio.entity.PortfolioValue;
import com.sever.portfolio.exception.AssertionUtil;
import com.sever.portfolio.external.BorsaGundemService;
import com.sever.portfolio.repository.PortfolioRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        Portfolio portfolio = optionalPortfolio.get();
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
            Double portfolioCurrentValue = 0d;
            for (PortfolioItem portfolioItem : portfolio.getPortfolioItems()) {
                PortfolioItemValue portfolioItemValue = getBorsaGundemService().get(portfolioItem.getCompanyCode());
                portfolioItemValue.setPortfolioItem(portfolioItem);
                portfolioItemValue.refreshCurrentValue(portfolioItem.getAmount());
                portfolioItem.getPortfolioItemValues().add(portfolioItemValue);
                portfolioCurrentValue += portfolioItemValue.getCurrentValue();
            }
            if (portfolioCurrentValue != 0) {
                PortfolioValue portfolioValue = new PortfolioValue();
                portfolioValue.setPortfolio(portfolio);
                portfolioValue.setCurrentValue(portfolioCurrentValue);
                portfolio.getPortfolioValues().add(portfolioValue);
                getPortfolioRepository().save(portfolio);
            }
        }

        return getAll();
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
            Double value = 0d;
            LocalDateTime valueDate = null;
            if (portfolio.getPortfolioValues().iterator().hasNext()) {
                PortfolioValue portfolioValue = portfolio.getPortfolioValues().iterator().next();
                value = portfolioValue.getCurrentValue();
                valueDate = portfolioValue.getCreateTime();
            }
            PortfolioValuesDto portfolioValuesDto = PortfolioValuesDto.builder()
                    .name(portfolio.getName())
                    .value(value)
                    .createTime(valueDate)
                    .build();

            portfolioValuesDtos.add(portfolioValuesDto);
        }
        return portfolioValuesDtos;
    }

    @Transactional
    @Override
    public Portfolio addItem(String id, PortfolioItem portfolioItem) {
        Optional<Portfolio> optionalPortfolio = getPortfolioRepository().findById(id);
        AssertionUtil.assertDataNotFound(optionalPortfolio);
        Portfolio portfolio = optionalPortfolio.get();
        portfolioItem.setPortfolio(portfolio);
        portfolio.getPortfolioItems().add(portfolioItem);
        getPortfolioRepository().save(portfolio);
        return portfolio;
    }
}
