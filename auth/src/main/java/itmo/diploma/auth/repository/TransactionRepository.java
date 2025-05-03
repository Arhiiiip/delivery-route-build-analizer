package itmo.diploma.auth.repository;

import itmo.diploma.auth.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}