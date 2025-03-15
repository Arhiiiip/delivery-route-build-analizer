package itmo.diploma.research.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ProductSearchRequest {
    private String query;
    private Double minPrice;
    private Double maxPrice;

    public ProductSearchRequest(String query, Double minPrice, Double maxPrice) {
        this.query = query;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }
}