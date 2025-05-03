package itmo.diploma.auth.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "service_users", uniqueConstraints = @UniqueConstraint(columnNames = {"login", "service_name"}))
public class ServiceUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String login;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
}