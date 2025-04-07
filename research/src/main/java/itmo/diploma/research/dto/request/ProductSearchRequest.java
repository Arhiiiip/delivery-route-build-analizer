package itmo.diploma.research.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ProductSearchRequest {
    private String query;
    private double minPriceUsd;
    private double maxPriceUsd;
    private double minPriceEur;
    private double maxPriceEur;
    private double minPriceRub;
    private double maxPriceRub;
}