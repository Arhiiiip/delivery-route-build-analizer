package itmo.diploma.general.entity;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
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