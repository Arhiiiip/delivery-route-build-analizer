package itmo.diploma.auth.repository;

import itmo.diploma.auth.entity.ServiceUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ServiceUserRepository extends JpaRepository<ServiceUser, Long> {
    Optional<ServiceUser> findByLoginAndServiceName(String login, UUID serviceId);
}