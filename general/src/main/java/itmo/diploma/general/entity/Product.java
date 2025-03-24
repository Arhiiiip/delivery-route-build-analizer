package itmo.diploma.general.entity;

import lombok.*;

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

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", price=" + price + currency +
                ", url='" + url + '\'' +
                '}';
    }
}