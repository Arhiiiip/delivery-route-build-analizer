package itmo.diploma.analytics.dto.request;

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
public class ProductRequest {
    private List<Product> products;
    private String userRequirement;
}