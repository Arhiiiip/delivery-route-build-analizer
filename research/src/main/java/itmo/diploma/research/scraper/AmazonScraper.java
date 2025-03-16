package itmo.diploma.research.scraper;

import itmo.diploma.research.dto.request.ProductSearchRequest;
import itmo.diploma.research.entity.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AmazonScraper {

    private static final String BASE_URL = "https://www.amazon.com";

    public List<Product> scrapeProducts(ProductSearchRequest request) throws IOException {
        List<Product> products = new ArrayList<>();

        String url = buildSearchUrl(request);

        Document document = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .get();

        Elements productContainers = document.select("div[role=\"listitem\"]");

        for (Element product : productContainers) {
            Element link = product.select("a[href]").first();
            if (link != null) {
                String href = BASE_URL + link.attr("href");

                String productName = product.select("a[href] > h2").text();

                String price = product.select("div[data-cy=\"secondary-offer-recipe\"] > div > span").text();
                if (!price.contains("$")) {
                    //TODO отладить
                    price = product.select("span.a-price-whole").text() + ".";
                    price += product.select("span.a-price-fraction").text();
                    if (price == "" || price == null) {
                        break;
                    }
                } else {
                    price = price.split("\\$")[1].split(" ")[0];
                }

                Product newProduct = new Product(productName, price, href);
                products.add(newProduct);
            }
        }
        return products;
    }

    private String buildSearchUrl(ProductSearchRequest request) {
        StringBuilder url = new StringBuilder("https://www.amazon.com/s?k=");
        url.append(request.getQuery().replace(" ", "+"));

        if (request.getMinPrice() != null) {
            url.append("&low-price=").append(request.getMinPrice());
        }
        if (request.getMaxPrice() != null) {
            url.append("&high-price=").append(request.getMaxPrice());
        }

        return url.toString();
    }
}
