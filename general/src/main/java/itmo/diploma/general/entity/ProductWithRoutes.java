package itmo.diploma.general.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductWithRoutes {

    private Product product;
    private List<String> countryRoute;
    private long cost;
    private String logisticCompanyName;
    private long hoursForDelivery;

    public ProductWithRoutes() {
    }

    public ProductWithRoutes(Product product, List<String> countryRoute, long cost,
                             String logisticCompanyName, long hoursForDelivery) {
        this.product = product;
        this.countryRoute = countryRoute;
        this.cost = cost;
        this.logisticCompanyName = logisticCompanyName;
        this.hoursForDelivery = hoursForDelivery;
    }

    @Override
    public String toString() {
        return "ProductWithRoutes{" +
                "product=" + product +
                ", countryRoute=" + countryRoute +
                ", cost=" + cost +
                ", logisticCompanyName='" + logisticCompanyName + '\'' +
                ", hoursForDelivery=" + hoursForDelivery +
                '}';
    }
}
