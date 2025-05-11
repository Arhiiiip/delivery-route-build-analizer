package itmo.diploma.auth.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "service_users", uniqueConstraints = @UniqueConstraint(columnNames = {"login", "service_name"}))
public class ServiceUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private UUID serviceId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String login) { this.email = login; }
    public UUID getServiceId() { return serviceId; }
    public void setServiceId(UUID serviceId) { this.serviceId = serviceId; }
}