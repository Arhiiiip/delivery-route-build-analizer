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

    public List<Product> scrapeProducts(ProductSearchRequest request) throws IOException {
        List<Product> products = new ArrayList<>();

        String url = buildSearchUrl(request);

        Document document = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .get();

        Elements productContainers = document.select("div.serverList-item");

        for (Element container : productContainers) {
            Elements productElements = container.select("div[data-cs-name='snippet-card']");

            for (Element productElement : productElements) {
                String name = productElement.select("a.EQlfk span[data-auto='snippet-title']").text();

                String priceText = productElement.select("span[data-autotest-value]").text();
                priceText = priceText.replaceAll("[^\\d.]", "");
                Double price = priceText.isEmpty() ? null : Double.parseDouble(priceText);

                String productUrl = productElement.select("a.EQlfk").attr("href");

                if (price != null && (request.getMinPrice() == null || price >= request.getMinPrice()) &&
                        (request.getMaxPrice() == null || price <= request.getMaxPrice())) {
                    products.add(new Product(name, price, "https://market.yandex.ru" + productUrl));
                }
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