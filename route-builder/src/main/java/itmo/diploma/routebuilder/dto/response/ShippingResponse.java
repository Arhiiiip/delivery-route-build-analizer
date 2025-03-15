package itmo.diploma.routebuilder.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShippingResponse {
    private String carrier;
    private BigDecimal cost;
    private String distance;
    private String duration;
    private boolean isAvailable;
    private String estimatedDeliveryDate;
}