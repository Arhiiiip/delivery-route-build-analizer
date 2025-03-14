package itmo.diploma.research.dto.response;

import java.util.List;

public class WalmartResponse {
    private List<WalmartItem> results;

    public List<WalmartItem> getResults() {
        return results;
    }

    public void setResults(List<WalmartItem> results) {
        this.results = results;
    }

    public static class WalmartItem {
        private String link;
        private double price;
        private String country;

        // Getters and Setters
        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }
}
