package itmo.diploma.general.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import itmo.diploma.general.dto.request.AnalizeRequest;
import itmo.diploma.general.dto.request.ConvertedPriceRequest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;

import itmo.diploma.general.entity.Currency;
import itmo.diploma.general.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CurrencyConversionService {

    @Value("${api.products.search.url}")
    private String API_URL;
    private final ObjectMapper objectMapper;
    private final ExchangeRateService exchangeRateService;

    @Autowired
    public CurrencyConversionService(ExchangeRateService exchangeRateService, RestTemplate restTemplate) {
        this.exchangeRateService = exchangeRateService;
        this.objectMapper = new ObjectMapper();

    }

    public List<Product> processPriceRequest(AnalizeRequest request) throws IOException, InterruptedException {
        ConvertedPriceRequest convertedRequest = convertPrices(request);

        String jsonRequest = objectMapper.writeValueAsString(convertedRequest);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(jsonRequest))
                .build();

        HttpResponse<String> response = client.send(httpRequest, BodyHandlers.ofString());

        List<Product> products = objectMapper.readValue(response.body(), objectMapper.getTypeFactory().constructCollectionType(List.class, Product.class));

        return products;
    }

    private ConvertedPriceRequest convertPrices(AnalizeRequest request) {
        ConvertedPriceRequest converted = new ConvertedPriceRequest();
        converted.setQuery(request.getQuery());

        Currency inputCurrency = request.getCurrency();
        double minPrice = request.getMinPrice();
        double maxPrice = request.getMaxPrice();

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
