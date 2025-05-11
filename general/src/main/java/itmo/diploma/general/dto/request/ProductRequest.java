package itmo.diploma.general.dto.request;

import itmo.diploma.general.entity.ProductWithRoutes;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class ProductRequest {
    private String userRequirement;
    private List<ProductWithRoutes> productsWithRoutes;

    public ProductRequest() {}

    public ProductRequest(String userRequirement, List<ProductWithRoutes> productsWithRoutes) {
        this.userRequirement = userRequirement;
        this.productsWithRoutes = productsWithRoutes;
    }
}
