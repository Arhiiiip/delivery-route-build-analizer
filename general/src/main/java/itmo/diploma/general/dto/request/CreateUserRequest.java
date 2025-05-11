package itmo.diploma.general.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CreateUserRequest {

    private String login;
    private String apiKey;

    public CreateUserRequest() {
    }

    public CreateUserRequest(String login, String apiKey) {
        this.login = login;
        this.apiKey = apiKey;
    }

    @Override
    public String toString() {
        return "CreateUserRequest{" +
                "login='" + login + '\'' +
                ", apiKey='" + (apiKey != null ? "***" : null) + '\'' +
                '}';
    }
}
