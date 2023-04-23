package com.sever.portfolio.service;

import com.sever.portfolio.entity.*;
import com.sever.portfolio.external.BorsaGundemService;
import com.sever.portfolio.repository.PortfolioRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class PortfolioServiceImplTest {

    @InjectMocks
    PortfolioServiceImpl portfolioService;

    @Mock
    PortfolioRepository portfolioRepository;

    @Mock
    BorsaGundemService borsaGundemService;

    @DisplayName("Trend UP")
    @Test
    void getEvaluation_01() {
        List<Portfolio> portfolioList = new ArrayList<>();
        Portfolio portfolio = new Portfolio();
        portfolio.setName("mock-portfolio");
        portfolioList.add(portfolio);

        PortfolioItem portfolioItem = new PortfolioItem();
        portfolioItem.setAmount(100l);
        portfolioItem.setCompanyCode("AKSEN");
        portfolio.getPortfolioItems().add(portfolioItem);

        PortfolioValue portfolioValue = new PortfolioValue();
        portfolioValue.setCurrentValue(3000.00d);
        portfolio.getPortfolioValues().add(portfolioValue);

        Mockito.when(portfolioRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime"))).thenReturn(portfolioList);

        PortfolioItemValue portfolioItemValue = new PortfolioItemValue();
        portfolioItemValue.setCurrentPrice(31.50d);
        Mockito.when(borsaGundemService.getCompanyValue(anyString())).thenReturn(portfolioItemValue);

        portfolioService.getEvaluation();

        ArgumentCaptor<Portfolio> portfolioArgumentCaptor = ArgumentCaptor.forClass(Portfolio.class);
        verify(portfolioRepository).save(portfolioArgumentCaptor.capture());
        Portfolio portfolioArgumentCaptorValue = portfolioArgumentCaptor.getValue();

        Assertions.assertEquals(1, portfolioArgumentCaptorValue.getPortfolioValueTrends().size());
        Assertions.assertEquals(UpOrDown.UP, portfolioArgumentCaptorValue.getPortfolioValueTrends().get(0).getTrend());
        Assertions.assertEquals(2, portfolioArgumentCaptorValue.getPortfolioValues().size());
        Assertions.assertEquals(3150d, portfolioArgumentCaptorValue.getPortfolioValues().get(1).getCurrentValue());
        Assertions.assertEquals(1, portfolioArgumentCaptorValue.getPortfolioItems().get(0).getPortfolioItemValues().size());
    }

    @DisplayName("Trend 2Up")
    @Test
    void getEvaluation_02() {
        List<Portfolio> portfolioList = new ArrayList<>();
        Portfolio portfolio = new Portfolio();
        portfolio.setName("mock-portfolio");
        portfolioList.add(portfolio);

        PortfolioItem portfolioItem = new PortfolioItem();
        portfolioItem.setAmount(100l);
        portfolioItem.setCompanyCode("AKSEN");
        portfolio.getPortfolioItems().add(portfolioItem);

        PortfolioValue portfolioValue = new PortfolioValue();
        portfolioValue.setCurrentValue(3000.00d);
        portfolio.getPortfolioValues().add(portfolioValue);

        PortfolioValueTrend portfolioValueTrend = new PortfolioValueTrend();
        portfolioValueTrend.setTrend(UpOrDown.UP);
        portfolioValueTrend.setTrendInARow(1);
        portfolio.getPortfolioValueTrends().add(portfolioValueTrend);

        Mockito.when(portfolioRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime"))).thenReturn(portfolioList);

        PortfolioItemValue portfolioItemValue = new PortfolioItemValue();
        portfolioItemValue.setCurrentPrice(31.50d);
        Mockito.when(borsaGundemService.getCompanyValue(anyString())).thenReturn(portfolioItemValue);

        portfolioService.getEvaluation();

        ArgumentCaptor<Portfolio> portfolioArgumentCaptor = ArgumentCaptor.forClass(Portfolio.class);
        verify(portfolioRepository).save(portfolioArgumentCaptor.capture());
        Portfolio portfolioArgumentCaptorValue = portfolioArgumentCaptor.getValue();

        Assertions.assertEquals(1, portfolioArgumentCaptorValue.getPortfolioValueTrends().size());
        Assertions.assertEquals(UpOrDown.UP, portfolioArgumentCaptorValue.getPortfolioValueTrends().get(0).getTrend());
        Assertions.assertEquals(2, portfolioArgumentCaptorValue.getPortfolioValueTrends().get(0).getTrendInARow());
    }

    @DisplayName("Trend Up,Down")
    @Test
    void getEvaluation_03() {
        List<Portfolio> portfolioList = new ArrayList<>();
        Portfolio portfolio = new Portfolio();
        portfolio.setName("mock-portfolio");
        portfolioList.add(portfolio);

        PortfolioItem portfolioItem = new PortfolioItem();
        portfolioItem.setAmount(100l);
        portfolioItem.setCompanyCode("AKSEN");
        portfolio.getPortfolioItems().add(portfolioItem);

        PortfolioValue portfolioValue = new PortfolioValue();
        portfolioValue.setCurrentValue(3000.00d);
        portfolio.getPortfolioValues().add(portfolioValue);

        PortfolioValueTrend portfolioValueTrend = new PortfolioValueTrend();
        portfolioValueTrend.setTrend(UpOrDown.UP);
        portfolioValueTrend.setTrendInARow(1);
        portfolio.getPortfolioValueTrends().add(portfolioValueTrend);

        Mockito.when(portfolioRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime"))).thenReturn(portfolioList);

        PortfolioItemValue portfolioItemValue = new PortfolioItemValue();
        portfolioItemValue.setCurrentPrice(29.50d);
        Mockito.when(borsaGundemService.getCompanyValue(anyString())).thenReturn(portfolioItemValue);

        portfolioService.getEvaluation();

        ArgumentCaptor<Portfolio> portfolioArgumentCaptor = ArgumentCaptor.forClass(Portfolio.class);
        verify(portfolioRepository).save(portfolioArgumentCaptor.capture());
        Portfolio portfolioArgumentCaptorValue = portfolioArgumentCaptor.getValue();

        Assertions.assertEquals(2, portfolioArgumentCaptorValue.getPortfolioValueTrends().size());
        Assertions.assertEquals(UpOrDown.DOWN, portfolioArgumentCaptorValue.getPortfolioValueTrends().get(1).getTrend());
        Assertions.assertEquals(1, portfolioArgumentCaptorValue.getPortfolioValueTrends().get(1).getTrendInARow());
    }

    @DisplayName("Trend DOWN")
    @Test
    void getEvaluation_04() {
        List<Portfolio> portfolioList = new ArrayList<>();
        Portfolio portfolio = new Portfolio();
        portfolio.setName("mock-portfolio");
        portfolioList.add(portfolio);

        PortfolioItem portfolioItem = new PortfolioItem();
        portfolioItem.setAmount(100l);
        portfolioItem.setCompanyCode("AKSEN");
        portfolio.getPortfolioItems().add(portfolioItem);

        PortfolioValue portfolioValue = new PortfolioValue();
        portfolioValue.setCurrentValue(3000.00d);
        portfolio.getPortfolioValues().add(portfolioValue);

        Mockito.when(portfolioRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime"))).thenReturn(portfolioList);

        PortfolioItemValue portfolioItemValue = new PortfolioItemValue();
        portfolioItemValue.setCurrentPrice(29.50d);
        Mockito.when(borsaGundemService.getCompanyValue(anyString())).thenReturn(portfolioItemValue);

        portfolioService.getEvaluation();

        ArgumentCaptor<Portfolio> portfolioArgumentCaptor = ArgumentCaptor.forClass(Portfolio.class);
        verify(portfolioRepository).save(portfolioArgumentCaptor.capture());
        Portfolio portfolioArgumentCaptorValue = portfolioArgumentCaptor.getValue();

        Assertions.assertEquals(1, portfolioArgumentCaptorValue.getPortfolioValueTrends().size());
        Assertions.assertEquals(UpOrDown.DOWN, portfolioArgumentCaptorValue.getPortfolioValueTrends().get(0).getTrend());
        Assertions.assertEquals(2, portfolioArgumentCaptorValue.getPortfolioValues().size());
        Assertions.assertEquals(2950d, portfolioArgumentCaptorValue.getPortfolioValues().get(1).getCurrentValue());
        Assertions.assertEquals(1, portfolioArgumentCaptorValue.getPortfolioItems().get(0).getPortfolioItemValues().size());
    }

    @DisplayName("Trend 2Down")
    @Test
    void getEvaluation_05() {
        List<Portfolio> portfolioList = new ArrayList<>();
        Portfolio portfolio = new Portfolio();
        portfolio.setName("mock-portfolio");
        portfolioList.add(portfolio);

        PortfolioItem portfolioItem = new PortfolioItem();
        portfolioItem.setAmount(100l);
        portfolioItem.setCompanyCode("AKSEN");
        portfolio.getPortfolioItems().add(portfolioItem);

        PortfolioValue portfolioValue = new PortfolioValue();
        portfolioValue.setCurrentValue(3000.00d);
        portfolio.getPortfolioValues().add(portfolioValue);

        PortfolioValueTrend portfolioValueTrend = new PortfolioValueTrend();
        portfolioValueTrend.setTrend(UpOrDown.DOWN);
        portfolioValueTrend.setTrendInARow(1);
        portfolio.getPortfolioValueTrends().add(portfolioValueTrend);

        Mockito.when(portfolioRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime"))).thenReturn(portfolioList);

        PortfolioItemValue portfolioItemValue = new PortfolioItemValue();
        portfolioItemValue.setCurrentPrice(29.50d);
        Mockito.when(borsaGundemService.getCompanyValue(anyString())).thenReturn(portfolioItemValue);

        portfolioService.getEvaluation();

        ArgumentCaptor<Portfolio> portfolioArgumentCaptor = ArgumentCaptor.forClass(Portfolio.class);
        verify(portfolioRepository).save(portfolioArgumentCaptor.capture());
        Portfolio portfolioArgumentCaptorValue = portfolioArgumentCaptor.getValue();

        Assertions.assertEquals(1, portfolioArgumentCaptorValue.getPortfolioValueTrends().size());
        Assertions.assertEquals(UpOrDown.DOWN, portfolioArgumentCaptorValue.getPortfolioValueTrends().get(0).getTrend());
        Assertions.assertEquals(2, portfolioArgumentCaptorValue.getPortfolioValueTrends().get(0).getTrendInARow());
    }

    @DisplayName("Trend Down,Up")
    @Test
    void getEvaluation_06() {
        List<Portfolio> portfolioList = new ArrayList<>();
        Portfolio portfolio = new Portfolio();
        portfolio.setName("mock-portfolio");
        portfolioList.add(portfolio);

        PortfolioItem portfolioItem = new PortfolioItem();
        portfolioItem.setAmount(100l);
        portfolioItem.setCompanyCode("AKSEN");
        portfolio.getPortfolioItems().add(portfolioItem);

        PortfolioValue portfolioValue = new PortfolioValue();
        portfolioValue.setCurrentValue(3000.00d);
        portfolio.getPortfolioValues().add(portfolioValue);

        PortfolioValueTrend portfolioValueTrend = new PortfolioValueTrend();
        portfolioValueTrend.setTrend(UpOrDown.DOWN);
        portfolioValueTrend.setTrendInARow(1);
        portfolio.getPortfolioValueTrends().add(portfolioValueTrend);

        Mockito.when(portfolioRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime"))).thenReturn(portfolioList);

        PortfolioItemValue portfolioItemValue = new PortfolioItemValue();
        portfolioItemValue.setCurrentPrice(31.50d);
        Mockito.when(borsaGundemService.getCompanyValue(anyString())).thenReturn(portfolioItemValue);

        portfolioService.getEvaluation();

        ArgumentCaptor<Portfolio> portfolioArgumentCaptor = ArgumentCaptor.forClass(Portfolio.class);
        verify(portfolioRepository).save(portfolioArgumentCaptor.capture());
        Portfolio portfolioArgumentCaptorValue = portfolioArgumentCaptor.getValue();

        Assertions.assertEquals(2, portfolioArgumentCaptorValue.getPortfolioValueTrends().size());
        Assertions.assertEquals(UpOrDown.UP, portfolioArgumentCaptorValue.getPortfolioValueTrends().get(1).getTrend());
        Assertions.assertEquals(1, portfolioArgumentCaptorValue.getPortfolioValueTrends().get(1).getTrendInARow());
    }
}