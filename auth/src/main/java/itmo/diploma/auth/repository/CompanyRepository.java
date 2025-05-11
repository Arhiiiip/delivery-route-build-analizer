package itmo.diploma.auth.repository;

import itmo.diploma.auth.entity.ApiKey;
import itmo.diploma.auth.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByName(String name);
}