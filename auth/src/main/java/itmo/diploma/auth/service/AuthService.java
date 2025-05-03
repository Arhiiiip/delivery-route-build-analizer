package itmo.diploma.auth.service;

import itmo.diploma.auth.entity.ApiKey;
import itmo.diploma.auth.entity.DurationType;
import itmo.diploma.auth.entity.ServiceUser;
import itmo.diploma.auth.repository.ApiKeyRepository;
import itmo.diploma.auth.repository.ServiceUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final ApiKeyRepository apiKeyRepository;
    private final ServiceUserRepository serviceUserRepository;
    private final PaymentService paymentService;

    @Autowired
    public AuthService(ApiKeyRepository apiKeyRepository,
                       ServiceUserRepository serviceUserRepository,
                       PaymentService paymentService) {
        this.apiKeyRepository = apiKeyRepository;
        this.serviceUserRepository = serviceUserRepository;
        this.paymentService = paymentService;
    }

    public boolean validateApiKey(String apiKey) {
        Optional<ApiKey> keyOpt = apiKeyRepository.findByKeyValue(apiKey);
        if (keyOpt.isEmpty()) {
            return false;
        }
        ApiKey key = keyOpt.get();
        return key.isActive() && key.getExpiresAt().isAfter(LocalDateTime.now());
    }

    public ServiceUser addUser(String login, String apiKey) {
        Optional<ApiKey> keyOpt = apiKeyRepository.findByKeyValue(apiKey);
        if (keyOpt.isEmpty() || !validateApiKey(apiKey)) {
            throw new IllegalArgumentException("Invalid or expired API key");
        }

        String serviceName = keyOpt.get().getServiceName();
        Optional<ServiceUser> existingUser = serviceUserRepository.findByLoginAndServiceName(login, serviceName);
        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        ServiceUser user = new ServiceUser();
        user.setLogin(login);
        user.setServiceName(serviceName);
        return serviceUserRepository.save(user);
    }

    public String createApiKey(String serviceName) {
        if (apiKeyRepository.findAll().stream().anyMatch(k -> k.getServiceName().equals(serviceName))) {
            throw new IllegalArgumentException("Service name already exists");
        }

        PaymentService.PaymentResult paymentResult = paymentService.processPayment(serviceName);
        Long transactionId = paymentResult.getTransactionId();
        DurationType duration = paymentResult.getDuration();

        String keyValue = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMonths(duration.getMonths());

        ApiKey apiKey = new ApiKey();
        apiKey.setKeyValue(keyValue);
        apiKey.setServiceName(serviceName);
        apiKey.setCreatedAt(now);
        apiKey.setExpiresAt(expiresAt);
        apiKey.setActive(true);
        apiKey.setTransactionId(transactionId);

        apiKeyRepository.save(apiKey);
        return keyValue;
    }
}