package itmo.diploma.routebuilder.service.companyservice;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
public class DhlService {

    private static final String DHL_API_URL = "https://api.dhl.com/rates";

    public BigDecimal getShippingCost(String origin, String destination, BigDecimal weight, String apiKey) {
        RestTemplate restTemplate = new RestTemplate();
        DhlRequest request = new DhlRequest(origin, destination, weight);
        DhlResponse response = restTemplate.postForObject(
                DHL_API_URL, request, DhlResponse.class, apiKey);
        return response.getCost();
    }
}
