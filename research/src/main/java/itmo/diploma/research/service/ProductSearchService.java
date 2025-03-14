package itmo.diploma.research.service;

import itmo.diploma.research.dto.request.ProductSearchRequest;
import itmo.diploma.research.entity.Product;
import itmo.diploma.research.scraper.YandexMarketScraper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductSearchService {

    YandexMarketScraper scraper = new YandexMarketScraper();


    public List<Product> searchProducts(ProductSearchRequest query) throws IOException, InterruptedException {
        List<Product> results = new ArrayList<>();

        results.addAll(searchYandex(query));

        return results;
    }

    private List<Product> searchYandex(ProductSearchRequest query) throws IOException, InterruptedException {
        return scraper.scrapeProducts(query);
    }
}