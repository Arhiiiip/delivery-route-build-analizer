package itmo.diploma.research.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;

@Data
@AllArgsConstructor
@Getter
@Setter
public class Product {
    private String name;
    private String price;
    private String url;

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", url='" + url + '\'' +
                '}';
    }
}