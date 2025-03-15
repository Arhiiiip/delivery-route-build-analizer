package itmo.diploma.routebuilder.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class Route {
    private String carrier;
    private BigDecimal cost;
    private int duration;
    private String origin;
    private String destination;

    public Route(String carrier, BigDecimal cost, int duration, String origin, String destination){
        this.carrier = carrier;
        this.cost = cost;
        this.duration = duration;
        this.origin = origin;
        this.destination = destination;
    }
}