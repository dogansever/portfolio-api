package com.sever.portfolio.web;

import com.sever.portfolio.entity.Portfolio;
import com.sever.portfolio.entity.PortfolioItem;
import com.sever.portfolio.service.PortfolioService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portfolio")
@AllArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping("/values")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse getValues() {
        return BaseResponse.createNew(portfolioService.getValues());
    }

    @GetMapping("/evaluation")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse getEvaluation() {
        return BaseResponse.createNew(portfolioService.calculatePortfolioEvaluations());
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse getAll() {
        return BaseResponse.createNew(portfolioService.getAll());
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public BaseResponse newPortfolio(@RequestBody @Validated Portfolio portfolio) {
        return BaseResponse.createNew(portfolioService.newPortfolio(portfolio));
    }

    @PostMapping("/{id}/item")
    @ResponseStatus(HttpStatus.CREATED)
    public BaseResponse addItem(@PathVariable("id") String id, @RequestBody @Validated PortfolioItem portfolioItem) {
        return BaseResponse.createNew(portfolioService.addItem(id, portfolioItem));
    }

    @DeleteMapping("/{id}/item/{item-id}")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse delete(@PathVariable("id") String id, @PathVariable("item-id") String itemId) {
        portfolioService.deleteItem(id, itemId);
        return BaseResponse.createNew("");
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse delete(@PathVariable("id") String id) {
        portfolioService.delete(id);
        return BaseResponse.createNew("");
    }
}
