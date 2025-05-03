package itmo.diploma.auth.service;

import itmo.diploma.auth.entity.DurationType;
import itmo.diploma.auth.entity.Transaction;
import itmo.diploma.auth.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public PaymentService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // Заглушка: имитация успешной оплаты с созданием чека и определением срока действия
    public PaymentResult processPayment(String serviceName) {
        String transactionNumber = "TXN-" + UUID.randomUUID().toString().substring(0, 8);
        DurationType duration = simulatePaymentPlan();
        String description = "Payment for API key for " + serviceName + " with duration " + duration;

        Transaction transaction = new Transaction();
        transaction.setTransactionNumber(transactionNumber);
        transaction.setDescription(description);

        Transaction savedTransaction = transactionRepository.save(transaction);
        System.out.println("Payment processed: " + transactionNumber);

        return new PaymentResult(savedTransaction.getId(), duration);
    }

    private DurationType simulatePaymentPlan() {
        int random = (int) (Math.random() * 3);
        return switch (random) {
            case 0 -> DurationType.MONTH;
            case 1 -> DurationType.SIX_MONTHS;
            case 2 -> DurationType.YEAR;
            default -> DurationType.MONTH;
        };
    }

    public static class PaymentResult {
        private final Long transactionId;
        private final DurationType duration;

        public PaymentResult(Long transactionId, DurationType duration) {
            this.transactionId = transactionId;
            this.duration = duration;
        }

        public Long getTransactionId() { return transactionId; }
        public DurationType getDuration() { return duration; }
    }
}