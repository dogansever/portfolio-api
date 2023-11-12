package com.sever.portfolio.config;

import com.sever.portfolio.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {

    private final PortfolioService portfolioService;

    @Scheduled(cron = "0 0 22 * * MON-FRI", zone = "GMT+3")
    public void getEvaluation() {
        log.info("getEvaluation started...");
        portfolioService.calculatePortfolioEvaluations();
        log.info("getEvaluation completed");
    }

    @Scheduled(cron = "0 0 0/1 * * MON-FRI", zone = "GMT+3")
    public void hourly() {
        log.info("Hourly chime started...");
    }
}
