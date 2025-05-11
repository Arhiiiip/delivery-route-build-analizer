package itmo.diploma.general.dto.request;

import itmo.diploma.general.entity.Priority;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class DeliveryRequest {
    private String fromCountry;
    private String toCountry;
    private double weight;
    private Priority priority;

    public DeliveryRequest() {}

    public DeliveryRequest(String fromCountry, String toCountry, double weight, Priority priority) {
        this.fromCountry = fromCountry;
        this.toCountry = toCountry;
        this.weight = weight;
        this.priority = priority;
    }
}
