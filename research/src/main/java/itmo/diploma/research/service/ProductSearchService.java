package itmo.diploma.research.service;

import itmo.diploma.research.dto.request.ProductSearchRequest;
import itmo.diploma.research.entity.Product;
import itmo.diploma.research.scraper.AmazonScraper;
import itmo.diploma.research.scraper.YandexMarketScraper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductSearchService {

    YandexMarketScraper yandexScraper = new YandexMarketScraper();
    AmazonScraper amazonScraper = new AmazonScraper();


    public List<Product> searchProducts(ProductSearchRequest query) throws IOException {
        List<Product> results = new ArrayList<>();

        results.addAll(searchYandex(query));
        results.addAll(searchAmazon(query));

        return results;
    }

    private List<Product> searchYandex(ProductSearchRequest query) throws IOException {
        return yandexScraper.scrapeProducts(query);
    }

    private List<Product> searchAmazon(ProductSearchRequest query) throws IOException {
        return amazonScraper.scrapeProducts(query);
    }
}