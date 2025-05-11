package itmo.diploma.general.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import itmo.diploma.general.dto.request.AnalizeRequest;
import itmo.diploma.general.dto.request.ConvertedPriceRequest;
import itmo.diploma.general.entity.Currency;
import itmo.diploma.general.entity.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class CurrencyConversionService {

    @Value("${api.products.search.url}")
    private String API_URL;
    private final ObjectMapper objectMapper;
    private final ExchangeRateService exchangeRateService;

    public CurrencyConversionService(ExchangeRateService exchangeRateService, ObjectMapper objectMapper) {
        this.exchangeRateService = exchangeRateService;
        this.objectMapper = objectMapper;
    }

    public List<Product> processPriceRequest(AnalizeRequest request) throws IOException, InterruptedException {
        ConvertedPriceRequest convertedRequest = convertPrices(request);

        String jsonRequest = objectMapper.writeValueAsString(convertedRequest);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        List<Product> products = objectMapper.readValue(response.body(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, Product.class));

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
            converted.setMinPriceEur(minPrice * exchangeRateService.getRate(Currency.USD, Currency.EUR));
            converted.setMaxPriceEur(maxPrice * exchangeRateService.getRate(Currency.USD, Currency.EUR));
        } else if (inputCurrency == Currency.RUB) {
            converted.setMinPriceRub(minPrice);
            converted.setMaxPriceRub(maxPrice);
            converted.setMinPriceUsd(minPrice * exchangeRateService.getRate(Currency.RUB, Currency.USD));
            converted.setMaxPriceUsd(maxPrice * exchangeRateService.getRate(Currency.RUB, Currency.USD));
            converted.setMinPriceEur(minPrice * exchangeRateService.getRate(Currency.RUB, Currency.EUR));
            converted.setMaxPriceEur(maxPrice * exchangeRateService.getRate(Currency.RUB, Currency.EUR));
        } else if (inputCurrency == Currency.EUR) {
            double usdRate = exchangeRateService.getRate(Currency.EUR, Currency.USD);
            double rubRate = exchangeRateService.getRate(Currency.EUR, Currency.RUB);
            converted.setMinPriceEur(minPrice);
            converted.setMaxPriceEur(maxPrice);
            converted.setMinPriceUsd(minPrice * usdRate);
            converted.setMaxPriceUsd(maxPrice * usdRate);
            converted.setMinPriceRub(minPrice * rubRate);
            converted.setMaxPriceRub(maxPrice * rubRate);
        }

        return converted;
    }
}
