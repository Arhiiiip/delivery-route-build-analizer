package itmo.diploma.general.dto.response;

import itmo.diploma.general.entity.ProductRecommendation;
import itmo.diploma.general.entity.ProductWithRoutes;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class ProductResponse {

    private ProductWithRoutes recommendedProduct;
    private List<String> deliveryRoute;
    private float estimatedCost;
    private int estimatedDays;
    private String recommendationReason;
    private List<ProductRecommendation> recommendations;

    public ProductResponse() {
    }
}
