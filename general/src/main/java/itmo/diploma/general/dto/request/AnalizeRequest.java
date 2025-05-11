package itmo.diploma.general.dto.request;

import itmo.diploma.general.entity.Currency;
import itmo.diploma.general.entity.Priority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalizeRequest {
    private String query;
    private double minPrice;
    private double maxPrice;
    private Currency currency;
    private String apiKey;
    private String loginUser;
    private Priority priority;
    private String userRequirement;
}
