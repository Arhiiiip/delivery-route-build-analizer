package itmo.diploma.general.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductRecommendation {

    private ProductWithRoutes product;
    private List<String> deliveryRoute;
    private float estimatedCost;
    private int estimatedDays;
    private String recommendationReason;
    private float score;

    public ProductRecommendation() {
    }
}
