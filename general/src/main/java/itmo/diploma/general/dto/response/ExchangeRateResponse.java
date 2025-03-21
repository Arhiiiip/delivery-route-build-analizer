package itmo.diploma.general.dto.response;

public class ExchangeRateResponse {
    private String result;
    private String errorType; // Для обработки ошибок
    private String baseCode;
    private java.util.Map<String, Double> conversionRates;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getBaseCode() {
        return baseCode;
    }

    public void setBaseCode(String baseCode) {
        this.baseCode = baseCode;
    }

    public java.util.Map<String, Double> getConversionRates() {
        return conversionRates;
    }

    public void setConversionRates(java.util.Map<String, Double> conversionRates) {
        this.conversionRates = conversionRates;
    }
}
