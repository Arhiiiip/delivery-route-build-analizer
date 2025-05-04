package itmo.diploma.routebuilder.service.companyservice;

import itmo.diploma.routebuilder.dto.response.ShippingResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DhlService {

    private static final String DHL_API_URL = "https://api.dhl.com/rates";

    public List<ShippingResponse> getShippingCost(String origin, String destination, Long weight, String apiKey) {
        RestTemplate restTemplate = new RestTemplate();
        DhlRequest request = new DhlRequest(origin, destination, weight);
        DhlResponse response = restTemplate.postForObject(
                DHL_API_URL, request, DhlResponse.class, apiKey);
        return response.getCost();
    }
}
