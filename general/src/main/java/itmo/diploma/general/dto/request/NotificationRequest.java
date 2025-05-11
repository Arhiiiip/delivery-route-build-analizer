package itmo.diploma.general.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class NotificationRequest {
    private Object data;
    private String email;

    public NotificationRequest() {}

    public NotificationRequest(Object data, String email) {
        this.data = data;
        this.email = email;
    }
}
