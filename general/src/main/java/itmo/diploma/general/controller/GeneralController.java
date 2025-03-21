package itmo.diploma.general.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/currency")
public class GeneralController {

    private final CurrencyConversionService currencyConversionService;

    @Autowired
    public CurrencyController(CurrencyConversionService currencyConversionService) {
        this.currencyConversionService = currencyConversionService;
    }

    @PostMapping("/convert")
    public List<Product> convertAndSearch(@RequestBody PriceRequest request) {
        return currencyConversionService.processPriceRequest(request);
    }
}