package itmo.diploma.general.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ServiceUser {
    private Long id;
    private String email;
    private UUID serviceId;

    public ServiceUser() {}
}
