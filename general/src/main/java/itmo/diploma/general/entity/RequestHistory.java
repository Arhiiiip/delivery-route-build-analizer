package itmo.diploma.general.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "requests")
@Getter
@Setter
public class RequestHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "request", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String request;

    @Column(name = "time", nullable = false)
    private Timestamp time;
}
