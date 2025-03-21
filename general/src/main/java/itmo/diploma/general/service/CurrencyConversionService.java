package itmo.diploma.general.service;

import itmo.diploma.general.dto.request.ConvertedPriceRequest;
import itmo.diploma.general.dto.request.ProductSearchRequest;
import itmo.diploma.general.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CurrencyConversionService {

    private final ExchangeRateService exchangeRateService;
    private final RestTemplate restTemplate;

    @Autowired
    public CurrencyConversionService(ExchangeRateService exchangeRateService, RestTemplate restTemplate) {
        this.exchangeRateService = exchangeRateService;
        this.restTemplate = restTemplate;
    }

    public List<Product> processPriceRequest(PriceRequest request) {
        ConvertedPriceRequest convertedRequest = convertPrices(request);

        // Отправляем запрос в ProductController
        String productServiceUrl = "http://localhost:8080/api/products/search";
        ProductSearchRequest productSearchRequest = new ProductSearchRequest(
                convertedRequest.getQuery(),
                String.valueOf(convertedRequest.getMinPriceUsd()), // Предполагаем, что сервис принимает USD
                String.valueOf(convertedRequest.getMaxPriceUsd())
        );

        List<Product> products = restTemplate.postForObject(productServiceUrl, productSearchRequest, List.class);

        // Здесь можно продолжить работу с ответом
        return products;
    }

    private ConvertedPriceRequest convertPrices(PriceRequest request) {
        ConvertedPriceRequest converted = new ConvertedPriceRequest();
        converted.setQuery(request.getQuery());

        Currency inputCurrency = request.getCurrency();
        double minPrice = request.getMinPrice();
        double maxPrice = request.getMaxPrice();

        // Конвертация в USD и RUB
        if (inputCurrency == Currency.USD) {
            converted.setMinPriceUsd(minPrice);
            converted.setMaxPriceUsd(maxPrice);
            converted.setMinPriceRub(minPrice * exchangeRateService.getRate(Currency.USD, Currency.RUB));
            converted.setMaxPriceRub(maxPrice * exchangeRateService.getRate(Currency.USD, Currency.RUB));
        } else if (inputCurrency == Currency.RUB) {
            converted.setMinPriceRub(minPrice);
            converted.setMaxPriceRub(maxPrice);
            converted.setMinPriceUsd(minPrice * exchangeRateService.getRate(Currency.RUB, Currency.USD));
            converted.setMaxPriceUsd(maxPrice * exchangeRateService.getRate(Currency.RUB, Currency.USD));
        } else if (inputCurrency == Currency.EUR) {
            double usdRate = exchangeRateService.getRate(Currency.EUR, Currency.USD);
            double rubRate = exchangeRateService.getRate(Currency.EUR, Currency.RUB);
            converted.setMinPriceUsd(minPrice * usdRate);
            converted.setMaxPriceUsd(maxPrice * usdRate);
            converted.setMinPriceRub(minPrice * rubRate);
            converted.setMaxPriceRub(maxPrice * rubRate);
        }

        return converted;
    }
}
