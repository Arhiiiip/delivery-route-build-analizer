package itmo.diploma.analytics.entity;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
public class ProductRecommendation {
    private List<String> deliveryRoute;
    private double estimatedCost;
    private int estimatedDays;
    private String recommendationReason;
    private double score;
}
