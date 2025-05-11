package itmo.diploma.general.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ApiKeyRequest {

    private String companyName;

    public ApiKeyRequest() {
    }

    public ApiKeyRequest(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Override
    public String toString() {
        return "ApiKeyRequest{" +
                "companyName='" + companyName + '\'' +
                '}';
    }
}
