package itmo.diploma.research.dto.response;

import java.util.List;

public class EbayResponse {
    private List<EbayItem> items;

    public List<EbayItem> getItems() {
        return items;
    }

    public void setItems(List<EbayItem> items) {
        this.items = items;
    }

    public static class EbayItem {
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
