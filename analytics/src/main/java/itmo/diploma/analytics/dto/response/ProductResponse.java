package itmo.diploma.analytics.dto.response;

import itmo.diploma.analytics.entity.Product;
import itmo.diploma.analytics.entity.ProductRecommendation;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
public class ProductResponse {
    private Product recommendedProduct;
    private List<String> deliveryRoute;
    private double estimatedCost;
    private int estimatedDays;
    private String recommendationReason;
    private List<ProductRecommendation> recommendations;
}
