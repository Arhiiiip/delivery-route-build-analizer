package itmo.diploma.routebuilder.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;

@Data
public class ShippingRequest {
    private String name;
    private String price;
    private Currency currency;
    private String url;
    private String countyFrom;
    private String countyTo;
    private Long weight;
}