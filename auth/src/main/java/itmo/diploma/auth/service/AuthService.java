package itmo.diploma.auth.service;

import itmo.diploma.auth.entity.ApiKey;
import itmo.diploma.auth.entity.Company;
import itmo.diploma.auth.entity.DurationType;
import itmo.diploma.auth.entity.ServiceUser;
import itmo.diploma.auth.repository.ApiKeyRepository;
import itmo.diploma.auth.repository.CompanyRepository;
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
    private final CompanyRepository companyRepository;
    private final PaymentService paymentService;

    @Autowired
    public AuthService(ApiKeyRepository apiKeyRepository,
                       ServiceUserRepository serviceUserRepository,
                       PaymentService paymentService,
                       CompanyRepository companyRepository) {
        this.apiKeyRepository = apiKeyRepository;
        this.companyRepository = companyRepository;
        this.serviceUserRepository = serviceUserRepository;
        this.paymentService = paymentService;
    }

    public boolean validateApiKey(String apiKey) {
        Optional<ApiKey> keyOpt = apiKeyRepository.findByKeyValue(apiKey);
        if (keyOpt.isEmpty()) {
            return false;
        }
        ApiKey key = keyOpt.get();
        return key.getExpiresAt().isAfter(LocalDateTime.now());
    }

    public ServiceUser addUser(String email, String apiKey) {
        Optional<ApiKey> keyOpt = apiKeyRepository.findByKeyValue(apiKey);
        if (keyOpt.isEmpty() || !validateApiKey(apiKey)) {
            throw new IllegalArgumentException("Invalid or expired API key");
        }

        UUID serviceId = keyOpt.get().getServiceId();
        Optional<ServiceUser> existingUser = serviceUserRepository.findByLoginAndServiceName(email, serviceId);
        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        ServiceUser user = new ServiceUser();
        user.setEmail(email);
        user.setServiceId(serviceId);
        return serviceUserRepository.save(user);
    }

    public String createApiKey(String serviceName) {
        PaymentService.PaymentResult paymentResult = paymentService.processPayment();
        UUID transactionId = paymentResult.getTransactionId();
        DurationType duration = paymentResult.getDuration();

        Company company = companyRepository.findByName(serviceName).get();
        if (company == null) {
            companyRepository.save(new Company(serviceName));
        }
        company = companyRepository.findByName(serviceName).get();

        String keyValue = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMonths(duration.getMonths());

        ApiKey apiKey = new ApiKey();
        apiKey.setId(UUID.randomUUID());
        apiKey.setKeyValue(keyValue);
        apiKey.setServiceId(company.getId());
        apiKey.setCreatedAt(now);
        apiKey.setExpiresAt(expiresAt);
        apiKey.setTransactionId(transactionId);

        apiKeyRepository.save(apiKey);
        return keyValue;
    }
}