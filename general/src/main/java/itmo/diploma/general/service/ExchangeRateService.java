package itmo.diploma.general.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import itmo.diploma.general.entity.Currency;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExchangeRateService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    @Value("${exchange.rate.api.key}")
    private String apiKey;

    private static final String API_URL = "https://v6.exchangerate-api.com/v6/";

    public ExchangeRateService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public double getRate(Currency from, Currency to) {
        if (from == to) {
            return 1.0;
        }

        String url = API_URL + apiKey + "/latest/" + from.name();

        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            if (jsonResponse == null) {
                throw new IllegalStateException("Failed to fetch exchange rate: API returned null");
            }

            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            String result = rootNode.get("result").asText();
            if (!"success".equals(result)) {
                String errorType = rootNode.get("error-type").asText();
                throw new IllegalStateException("Failed to fetch exchange rate: " + errorType);
            }

            JsonNode conversionRates = rootNode.get("conversion_rates");
            Double rate = conversionRates.get(to.name()).asDouble();
            if (rate == null) {
                throw new IllegalArgumentException("Rate for currency " + to + " not found");
            }

            return rate;

        } catch (Exception e) {
            throw new IllegalStateException("Error fetching exchange rate from API", e);
        }
    }

}
