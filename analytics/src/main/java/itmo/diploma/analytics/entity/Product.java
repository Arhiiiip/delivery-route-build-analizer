package itmo.diploma.analytics.entity;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Product {
    private String name;
    private double price;
    private String url;
    private String originCountry;
    private String destinationCountry;
}