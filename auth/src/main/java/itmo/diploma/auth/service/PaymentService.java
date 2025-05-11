package itmo.diploma.auth.service;

import itmo.diploma.auth.entity.DurationType;
import itmo.diploma.auth.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {

    // Заглушка: имитация успешной оплаты с созданием чека и определением срока действия
    public PaymentResult processPayment() {
        UUID transactionId = UUID.randomUUID();
        DurationType duration = simulatePaymentPlan();

        System.out.println("Payment processed: " + transactionId);

        return new PaymentResult(transactionId, duration);
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
        private final UUID transactionId;
        private final DurationType duration;

        public PaymentResult(UUID transactionId, DurationType duration) {
            this.transactionId = transactionId;
            this.duration = duration;
        }

        public UUID getTransactionId() { return transactionId; }
        public DurationType getDuration() { return duration; }
    }
}