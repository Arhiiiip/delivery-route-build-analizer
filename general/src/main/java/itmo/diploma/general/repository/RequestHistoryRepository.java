package itmo.diploma.general.repository;

import itmo.diploma.general.entity.RequestHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RequestHistoryRepository extends JpaRepository<RequestHistory, UUID> {
}
