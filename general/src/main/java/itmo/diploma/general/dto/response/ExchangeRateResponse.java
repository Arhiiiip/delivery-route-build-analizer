package itmo.diploma.general.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Data
@Getter
@Setter
public class ExchangeRateResponse {
    private String result;
    private String errorType;
    private String baseCode;
    @JsonProperty("conversion_rates")
    private ConvertedRate conversionRates;

    class ConvertedRate {
        private double USD;
        private double EUR;
        private double RUB;
    }
}
