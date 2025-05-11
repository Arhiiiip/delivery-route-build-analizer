package itmo.diploma.general.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "tokens")
@Getter
@Setter
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "transaction", nullable = false)
    private UUID transaction;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "time_create", nullable = false)
    private Timestamp timeCreate;

    @Column(name = "lifetime", nullable = false)
    private Timestamp lifetime;
}
