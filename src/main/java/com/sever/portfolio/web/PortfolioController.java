package com.sever.portfolio.web;

import com.sever.portfolio.entity.Portfolio;
import com.sever.portfolio.entity.PortfolioItem;
import com.sever.portfolio.service.PortfolioService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/portfolio")
@AllArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping("/evaluation")
    @ResponseStatus(HttpStatus.OK)
    public List<Portfolio> getEvaluation() {
        return portfolioService.getEvaluation();
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<Portfolio> getAll() {
        return portfolioService.getAll();
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Portfolio newPortfolio(@RequestBody @Validated Portfolio portfolio) {
        return portfolioService.newPortfolio(portfolio);
    }

    @PostMapping("/{id}/item")
    @ResponseStatus(HttpStatus.CREATED)
    public Portfolio addItem(@PathVariable("id") String id, @RequestBody @Validated PortfolioItem portfolioItem) {
        return portfolioService.addItem(id,portfolioItem);
    }

    @DeleteMapping("/{id}/item/{item-id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") String id, @PathVariable("item-id") String itemId) {
        portfolioService.deleteItem(id, itemId);
    }

}
