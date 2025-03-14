package itmo.diploma.research.dto.request;

import lombok.Data;

@Data
public class ProductSearchRequest {
    private String query;
    private Double minPrice;
    private Double maxPrice;

    public ProductSearchRequest(String query, String category, String brand, Double minPrice, Double maxPrice, String country, String language, String sort, Integer limit) {
        this.query = query;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }
}