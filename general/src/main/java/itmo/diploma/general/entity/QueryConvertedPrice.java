package itmo.diploma.general.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class QueryConvertedPrice {
    private String query;
    private double minPriceUsd;
    private double maxPriceUsd;
    private double minPriceRub;
    private double maxPriceRub;
}
