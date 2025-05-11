package itmo.diploma.general.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class DeliveryResponse {
    private List<String> route;
    private long cost;
    private String company;
    private long hours;

    public DeliveryResponse() {}

    public List<String> getRoute() {
        return route;
    }
    public void setRoute(List<String> route) {
        this.route = route;
    }
    public long getCost() {
        return cost;
    }
    public void setCost(long cost) {
        this.cost = cost;
    }
    public String getCompany() {
        return company;
    }
    public void setCompany(String company) {
        this.company = company;
    }
    public long getHours() {
        return hours;
    }
    public void setHours(long hours) {
        this.hours = hours;
    }
}
