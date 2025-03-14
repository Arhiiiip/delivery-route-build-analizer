package itmo.diploma.research.entity;

import lombok.Data;

@Data
public class ProductInfo {
    private String site;
    private double price;
    private String country;

    public ProductInfo(String site, double price, String country) {
        this.site = site;
        this.price = price;
        this.country = country;
    }
}