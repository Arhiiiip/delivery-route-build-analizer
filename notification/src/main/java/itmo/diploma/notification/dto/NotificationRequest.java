package itmo.diploma.notification.dto;

public class NotificationRequest {
    private Object data;
    private String email;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
