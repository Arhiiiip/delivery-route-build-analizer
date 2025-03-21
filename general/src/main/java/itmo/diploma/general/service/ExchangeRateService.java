package itmo.diploma.general.service;

package com.example.demo.service;

import com.example.demo.model.Currency;
import itmo.diploma.general.dto.response.ExchangeRateResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExchangeRateService {

    private final RestTemplate restTemplate;

    // API-ключ из конфигурации (application.properties)
    @Value("${exchange.rate.api.key}")
    private String apiKey;

    // Базовый URL API
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/";

    public ExchangeRateService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double getRate(Currency from, Currency to) {
        if (from == to) {
            return 1.0;
        }

        // Формируем URL для запроса курса валют
        String url = API_URL + apiKey + "/latest/" + from.name();
        ExchangeRateResponse response = restTemplate.getForObject(url, ExchangeRateResponse.class);

        if (response == null || !"success".equals(response.getResult())) {
            throw new IllegalStateException("Failed to fetch exchange rate: " + (response != null ? response.getErrorType() : "Unknown error"));
        }

        // Получаем курс из ответа
        Double rate = response.getConversionRates().get(to.name());
        if (rate == null) {
            throw new IllegalArgumentException("Rate for currency " + to + " not found");
        }

        return rate;
    }

}
