package itmo.diploma.research.scraper;

import itmo.diploma.research.dto.request.ProductSearchRequest;
import itmo.diploma.research.entity.Product;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.TimeUnit;


import java.io.IOException;
import java.util.List;

public class GoogleShoppingScraper {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    @Value("${googleshopping.password}")
    public static String USERNAME;
    @Value("${googleshopping.mail}")
    public static String PASSWORD;
    private static final String BASE_URL = "https://www.google.com/shopping";

    public void scrapeProducts(ProductSearchRequest query) throws IOException, JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("source", "google_shopping_search");
        jsonObject.put("domain", "com");
        jsonObject.put("geo_location", "FR");
        jsonObject.put("query", query.getQuery());
        jsonObject.put("pages", 1);
        jsonObject.put("parse", true);
        jsonObject.put("context", new JSONArray()
                .put(new JSONObject()
                        .put("key", "sort_by")
                        .put("value", "pd"))
                .put(new JSONObject()
                        .put("key", "min_price")
                        .put("value", 20))
        );

        Authenticator authenticator = (route, response) -> {
            String credential = Credentials.basic(USERNAME, PASSWORD);
            return response
                    .request()
                    .newBuilder()
                    .header(AUTHORIZATION_HEADER, credential)
                    .build();
        };

        var client = new OkHttpClient.Builder()
                .authenticator(authenticator)
                .followRedirects(false)
                .followSslRedirects(false)
                .readTimeout(180, TimeUnit.SECONDS)
                .build();

        var mediaType = MediaType.parse("application/json; charset=utf-8");
        var body = RequestBody.create(jsonObject.toString(), mediaType);
        var request = new Request.Builder()
                .url("https://realtime.oxylabs.io/v1/queries")
                .post(body)
                .build();

        try (var response = client.newCall(request).execute()) {
            if (response.body() != null) {
                try (var responseBody = response.body()) {
                    System.out.println(responseBody.string());
                }
            }
        } catch (Exception exception) {
            System.out.println("Error: " + exception.getMessage());
        }

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
