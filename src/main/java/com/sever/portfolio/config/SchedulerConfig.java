package com.sever.portfolio.config;

import com.sever.portfolio.entity.Portfolio;
import com.sever.portfolio.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {

    private final PortfolioService portfolioService;

    @Scheduled(cron = "0 0 19 * * MON-FRI", zone = "GMT+3")
    public void getEvaluation() {
        log.info("getEvaluation started...");
        List<Portfolio> evaluation = portfolioService.getEvaluation();
        log.info("getEvaluation completed");
    }

    @Scheduled(cron = "0 0 0/1 * * MON-FRI", zone = "GMT+3")
    public void hourly() {
        log.info("Hourly chime started...");
    }
}
