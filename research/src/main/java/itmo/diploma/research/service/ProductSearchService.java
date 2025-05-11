package itmo.diploma.research.service;

import itmo.diploma.research.dto.request.ProductSearchRequest;
import itmo.diploma.research.entity.Product;
import itmo.diploma.research.scraper.AmazonScraper;
import itmo.diploma.research.scraper.GoogleShoppingScraper;
import itmo.diploma.research.scraper.YandexMarketScraper;
import org.json.JSONException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ProductSearchService {

    YandexMarketScraper yandexScraper = new YandexMarketScraper();
    AmazonScraper amazonScraper = new AmazonScraper();
    GoogleShoppingScraper googleShoppingScraper = new GoogleShoppingScraper();

    public List<Product> searchProducts(ProductSearchRequest query) throws IOException, JSONException {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        CompletableFuture<List<Product>> yandexFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return searchYandex(query);
            } catch (Exception e) {
                e.printStackTrace();
                return List.of();
            }
        }, executor);

        CompletableFuture<List<Product>> amazonFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return searchAmazon(query);
            } catch (Exception e) {
                e.printStackTrace();
                return List.of();
            }
        }, executor);

        CompletableFuture<List<Product>> googleFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return searchGoogle(query);
            } catch (Exception e) {
                e.printStackTrace();
                return List.of();
            }
        }, executor);

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(yandexFuture, amazonFuture, googleFuture);

        List<Product> results = allFutures.thenApply(v -> {
            List<Product> all = new ArrayList<>();
            all.addAll(yandexFuture.join());
            all.addAll(amazonFuture.join());
            all.addAll(googleFuture.join());
            return all;
        }).join();

        executor.shutdown();
        return results;
    }

    private List<Product> searchYandex(ProductSearchRequest query) throws IOException {
        return yandexScraper.scrapeProducts(query);
    }

    private List<Product> searchAmazon(ProductSearchRequest query) throws IOException {
        return amazonScraper.scrapeProducts(query);
    }

    private List<Product> searchGoogle(ProductSearchRequest query) throws IOException, JSONException {
        return googleShoppingScraper.scrapeProducts(query);
    }
}
