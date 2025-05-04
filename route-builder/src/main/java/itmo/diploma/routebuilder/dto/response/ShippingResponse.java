package itmo.diploma.routebuilder.dto.response;

import itmo.diploma.routebuilder.dto.request.ShippingRequest;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ShippingResponse {
    private ShippingRequest request;
    private List<String> countryRoute;
    private Long cost;
    private String logisticCompanyName;
    private String hoursForDelivery;
}