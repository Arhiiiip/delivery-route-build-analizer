package itmo.diploma.research.service;

import itmo.diploma.research.dto.request.ProductSearchRequest;
import itmo.diploma.research.dto.response.AmazonResponse;
import itmo.diploma.research.dto.response.EbayResponse;
import itmo.diploma.research.dto.response.GoogleShoppingResponse;
import itmo.diploma.research.dto.response.WalmartResponse;
import itmo.diploma.research.entity.ProductInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductSearchService {
    @Value("${rapidapi.key}")
    private static String RAPIDAPI_KEY;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<ProductInfo> searchProducts(ProductSearchRequest query) {
        List<ProductInfo> results = new ArrayList<>();

        // Поиск через Google Shopping API
        results.addAll(searchGoogleShopping(query));

        // Поиск через Amazon Product API
        results.addAll(searchAmazon(query));

        // Поиск через eBay API
        results.addAll(searchEbay(query));

        // Поиск через Walmart API
        results.addAll(searchWalmart(query));

        return results;
    }

    private List<ProductInfo> searchGoogleShopping(ProductSearchRequest query) {
        String url = "https://google-shopping-api.p.rapidapi.com/search?query=" + query;
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", RAPIDAPI_KEY);
        headers.set("X-RapidAPI-Host", "google-shopping-api.p.rapidapi.com");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<GoogleShoppingResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, GoogleShoppingResponse.class);

        if (response.getBody() != null) {
            return response.getBody().getResults().stream()
                    .map(item -> new ProductInfo(
                            item.getLink(),
                            item.getPrice(),
                            item.getCountry()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    private List<ProductInfo> searchAmazon(ProductSearchRequest query) {
        String url = "https://amazon-product-data1.p.rapidapi.com/search?query=" + query;
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", RAPIDAPI_KEY);
        headers.set("X-RapidAPI-Host", "amazon-product-data1.p.rapidapi.com");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<AmazonResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, AmazonResponse.class);

        if (response.getBody() != null) {
            return response.getBody().getResults().stream()
                    .map(item -> new ProductInfo(
                            item.getLink(),
                            item.getPrice(),
                            item.getCountry()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    private List<ProductInfo> searchEbay(ProductSearchRequest query) {
        String url = "https://ebay-search.p.rapidapi.com/search?q=" + query;
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", RAPIDAPI_KEY);
        headers.set("X-RapidAPI-Host", "ebay-search.p.rapidapi.com");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<EbayResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, EbayResponse.class);

        if (response.getBody() != null) {
            return response.getBody().getItems().stream()
                    .map(item -> new ProductInfo(
                            item.getLink(),
                            item.getPrice(),
                            item.getCountry()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    private List<ProductInfo> searchWalmart(ProductSearchRequest query) {
        String url = "https://walmart-product-data.p.rapidapi.com/search?query=" + query;
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", RAPIDAPI_KEY);
        headers.set("X-RapidAPI-Host", "walmart-product-data.p.rapidapi.com");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<WalmartResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, WalmartResponse.class);

        if (response.getBody() != null) {
            return response.getBody().getResults().stream()
                    .map(item -> new ProductInfo(
                            item.getLink(),
                            item.getPrice(),
                            item.getCountry()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}