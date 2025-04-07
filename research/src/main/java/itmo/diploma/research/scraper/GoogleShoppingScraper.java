package itmo.diploma.research.scraper;

import itmo.diploma.research.dto.request.ProductSearchRequest;

import org.json.JSONException;
import java.net.*;
import java.io.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GoogleShoppingScraper {
    public void scrapeProducts(ProductSearchRequest query) throws IOException, JSONException {
        try {
            String apiKey = "";
            String country = "fr";


            String apiUrl = "https://api.scrapingdog.com/google_shopping/?api_key=" + apiKey
                    + "&query=" + query.getQuery()
                    + "&country=" + country;

            if (query.getMinPriceUsd() != 0) {
                apiUrl += "&ppr_min=" + query.getMinPriceUsd();
            }
            if (query.getMaxPriceUsd() != 0) {
                apiUrl += "&ppr_max=" + query.getMaxPriceUsd();
            }

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = reader.readLine()) != null) {
                    response.append(inputLine);
                }

                reader.close();
                System.out.println(response.toString());
            } else {
                System.out.println("HTTP request failed with response code: " + responseCode);
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
