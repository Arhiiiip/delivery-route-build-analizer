package itmo.diploma.general.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ProductSearchRequest {
    private String query;
    private String minPrice;
    private String maxPrice;

    public ProductSearchRequest(String query, String minPrice, String maxPrice) {
        this.query = query;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }
}
