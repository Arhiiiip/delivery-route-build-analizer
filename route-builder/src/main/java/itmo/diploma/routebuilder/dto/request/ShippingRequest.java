package itmo.diploma.routebuilder.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShippingRequest {
    private String origin;
    private String destination;
    private BigDecimal weight;
    private String originCountry;
    private String destinationCountry;
    private String productType;
    private BigDecimal productValue;
    private String category;
}