package itmo.diploma.general.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itmo.diploma.general.dto.request.*;
import itmo.diploma.general.dto.response.DeliveryResponse;
import itmo.diploma.general.dto.response.ProductResponse;
import itmo.diploma.general.entity.*;
import itmo.diploma.general.repository.CompanyRepository;
import itmo.diploma.general.repository.RequestHistoryRepository;
import itmo.diploma.general.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class GeneralServiceImpl implements GeneralService {

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final RequestHistoryRepository requestHistoryRepository;
    private final CurrencyConversionService currencyConversionService;

    @Value("${api.auth.url}")
    private String authServiceUrl;

    @Value("${api.delivery.url}")
    private String deliveryServiceUrl;

    @Value("${api.analyze.url}")
    private String analyzeServiceUrl;

    @Value("${api.notify.url}")
    private String notifyServiceUrl;

    public GeneralServiceImpl(
            ObjectMapper objectMapper,
            UserRepository userRepository,
            CompanyRepository companyRepository,
            RequestHistoryRepository requestHistoryRepository,
            CurrencyConversionService currencyConversionService) {
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.requestHistoryRepository = requestHistoryRepository;
        this.currencyConversionService = currencyConversionService;
    }

    @Override
    public String getApiKey(String companyName) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(authServiceUrl + "/auth/createKey?serviceName=" + companyName))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    @Override
    public Long createUser(String login, String apiKey) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(authServiceUrl + "/auth/user?login=" + login + "&apiKey=" + apiKey))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ServiceUser serviceUser = objectMapper.readValue(response.body(), ServiceUser.class);
        return serviceUser.getId();
    }

    @Override
    public ProductResponse search(SearchRequest request) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest validateRequest = HttpRequest.newBuilder()
                .uri(URI.create(authServiceUrl + "/auth/validate?apiKey=" + request.getApiKey()))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> validateResponse = client.send(validateRequest, HttpResponse.BodyHandlers.ofString());

        Boolean isValid = Boolean.parseBoolean(validateResponse.body());
        if (!isValid) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid API key");
        }

        AnalizeRequest analizeRequest = new AnalizeRequest(
                request.getQuery(),
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getCurrency(),
                request.getApiKey(),
                request.getLoginUser(),
                request.getPriority(),
                request.getUserRequirement()
        );

        List<Product> products = currencyConversionService.processPriceRequest(analizeRequest);

        long randomWeight = new Random().nextInt(10) + 1;
        for (Product product : products) {
            product.setWeight(randomWeight);
        }

        List<ProductWithRoutes> productsWithRoutes = new ArrayList<>();
        for (Product product : products) {
            DeliveryRequest deliveryRequest = new DeliveryRequest(
                    product.getCountryFrom(),
                    product.getCountryTo(),
                    product.getWeight(),
                    request.getPriority()
            );

            String deliveryJson = objectMapper.writeValueAsString(deliveryRequest);
            HttpRequest deliveryHttpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(deliveryServiceUrl + "/delivery"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(deliveryJson))
                    .build();

            HttpResponse<String> deliveryResponse = client.send(deliveryHttpRequest, HttpResponse.BodyHandlers.ofString());

            DeliveryResponse delivery = objectMapper.readValue(deliveryResponse.body(), DeliveryResponse.class);

            ProductWithRoutes productWithRoutes = new ProductWithRoutes();
            productWithRoutes.setProduct(product);
            productWithRoutes.setCountryRoute(delivery.getRoute());
            productWithRoutes.setCost(delivery.getCost());
            productWithRoutes.setLogisticCompanyName(delivery.getCompany());
            productWithRoutes.setHoursForDelivery(delivery.getHours());

            productsWithRoutes.add(productWithRoutes);
        }

        ProductRequest productRequest = new ProductRequest();
        productRequest.setUserRequirement(request.getUserRequirement());
        productRequest.setProductsWithRoutes(productsWithRoutes);

        String analyzeJson = objectMapper.writeValueAsString(productRequest);
        HttpRequest analyzeHttpRequest = HttpRequest.newBuilder()
                .uri(URI.create(analyzeServiceUrl + "/recommend"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(analyzeJson))
                .build();

        HttpResponse<String> analyzeResponse = client.send(analyzeHttpRequest, HttpResponse.BodyHandlers.ofString());

        ProductResponse productResponse = objectMapper.readValue(analyzeResponse.body(), ProductResponse.class);

        NotificationRequest notificationRequest = new NotificationRequest(
                productResponse,
                request.getLoginUser()
        );

        String notifyJson = objectMapper.writeValueAsString(notificationRequest);
        HttpRequest notifyHttpRequest = HttpRequest.newBuilder()
                .uri(URI.create(notifyServiceUrl + "/notify"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(notifyJson))
                .build();

        client.send(notifyHttpRequest, HttpResponse.BodyHandlers.ofString());

        try {
            saveRequestHistory(request);
        } catch (Exception e) {
            System.err.println("Failed to save request history: " + e.getMessage());
        }

        return productResponse;
    }

    private void saveRequestHistory(SearchRequest request) throws JsonProcessingException {
        Company company = companyRepository.findByCompanyName(request.getCompanyName())
                .orElseThrow(() -> new IllegalArgumentException("Company not found: " + request.getCompanyName()));

        User user = userRepository.findByEmailAndCompanyId(request.getLoginUser(), company.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getLoginUser()));

        RequestHistory history = new RequestHistory();
        history.setUserId(user.getId());
        history.setRequest(objectMapper.writeValueAsString(request.withoutApiKey()));
        history.setTime(new Timestamp(System.currentTimeMillis()));

        requestHistoryRepository.save(history);
    }
}
