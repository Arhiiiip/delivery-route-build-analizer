package itmo.diploma.research.scraper;

import itmo.diploma.research.dto.request.ProductSearchRequest;
import itmo.diploma.research.entity.Currency;
import itmo.diploma.research.entity.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GoogleShoppingScraper {

    private static final String BASE_URL = "https://www.google.com/shopping";

    public List<Product> scrapeProducts(ProductSearchRequest request) throws IOException {
        List<Product> products = new ArrayList<>();

        String url = buildSearchUrl(request);

        Document document = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .header("Accept-Language", "en-GB,en;q=0.9")
                .get();

        Elements productContainers = document.select("div[role=\"button\"]");

        for (Element product : productContainers) {
            Element link = product.select("a[href]").first();
            if (link != null) {
                String href = BASE_URL + link.attr("href");

                String productName = product.select("h3.tAxDx").text();

                String price = product.select("span.a8Pemb").text();
                if (!price.contains("€")) {
                    price = product.select("span.a-price-whole").text() + ".";
                    price += product.select("span.a-price-fraction").text();
                    if (price == "" || price == null) {
                        break;
                    }
                } else {
                    price = price.split("€")[1].split(" ")[0];
                }

                Product newProduct = new Product(productName, price, Currency.EUR, href);
                products.add(newProduct);
            }
        }
        return products;
    }

    private String buildSearchUrl(ProductSearchRequest request) {
        StringBuilder url = new StringBuilder("https://www.google.com/search?tbm=shop&q=");
        url.append(request.getQuery().replace(" ", "+"));

        if (request.getMinPriceUsd() != 0) {
            url.append("&low-price=").append(request.getMinPriceUsd());
        }
        if (request.getMaxPriceUsd() != 0) {
            url.append("&high-price=").append(request.getMaxPriceUsd());
        }

        return url.toString();
    }
}
