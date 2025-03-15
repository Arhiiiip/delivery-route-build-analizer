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

public class YandexMarketScraper {

    private static final String BASE_URL = "https://market.yandex.ru";

    public List<Product> scrapeProducts(ProductSearchRequest request) throws IOException {
        List<Product> products = new ArrayList<>();

        String url = buildSearchUrl(request);

        Document document = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .get();

        Elements productContainers = document.select("div.serverList-item > div");

        for (Element product : productContainers) {
            Element link = product.select("a[href]").first();
            if (link != null) {
                String href = BASE_URL + link.attr("href");

                String productName = product.select("span[role=\"link\"]").text();

                String price = product.select("span.ds-text_color_price-term").text();
                if (price.split("₽").length > 2) {
                    continue;
                }

                Product newProduct = new Product(productName, price, href);
                products.add(newProduct);
            }
        }
        return products;
    }

    private String buildSearchUrl(ProductSearchRequest request) {
        StringBuilder url = new StringBuilder("https://market.yandex.ru/search?text=");
        url.append(request.getQuery().replace(" ", "+"));

        if (request.getMinPrice() != null) {
            url.append("&pricefrom=").append(request.getMinPrice());
        }
        if (request.getMaxPrice() != null) {
            url.append("&priceto=").append(request.getMaxPrice());
        }

        return url.toString();
    }
}