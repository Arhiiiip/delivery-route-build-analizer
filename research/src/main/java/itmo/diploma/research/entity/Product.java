package itmo.diploma.research.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Getter
@Setter
public class Product {
    private String name;
    private String price;
    private Currency currency;
    private String url;
    private String countyFrom;
    private String countyTo;
    private Long weight;

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", price=" + price + currency +
                ", url='" + url + '\'' +
                '}';
    }
}