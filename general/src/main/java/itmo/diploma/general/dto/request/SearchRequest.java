package itmo.diploma.general.dto.request;

import itmo.diploma.general.entity.Currency;
import itmo.diploma.general.entity.Priority;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SearchRequest {
    private String query;
    private double minPrice;
    private double maxPrice;
    private Currency currency;
    private String apiKey;
    private String loginUser;
    private String companyName;
    private Priority priority;
    private String userRequirement;

    public SearchRequest withoutApiKey() {
        SearchRequest copy = new SearchRequest();
        copy.setQuery(this.query);
        copy.setMinPrice(this.minPrice);
        copy.setMaxPrice(this.maxPrice);
        copy.setCurrency(this.currency);
        copy.setLoginUser(this.loginUser);
        copy.setCompanyName(this.companyName);
        copy.setPriority(this.priority);
        copy.setUserRequirement(this.userRequirement);
        return copy;
    }
}
