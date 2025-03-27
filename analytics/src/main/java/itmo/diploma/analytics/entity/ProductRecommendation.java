package itmo.diploma.analytics.entity;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
@Builder
public class ProductRecommendation {
    private Product product;
    private List<String> deliveryRoute;
    private double estimatedCost;
    private int estimatedDays;
    private String recommendationReason;
    private double score;
}
