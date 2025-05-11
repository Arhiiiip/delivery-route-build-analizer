package itmo.diploma.general.controller;

import itmo.diploma.general.dto.request.ApiKeyRequest;
import itmo.diploma.general.dto.request.CreateUserRequest;
import itmo.diploma.general.dto.request.SearchRequest;
import itmo.diploma.general.dto.response.ProductResponse;
import itmo.diploma.general.service.GeneralServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeneralController {

    private final GeneralServiceImpl generalService;

    public GeneralController(GeneralServiceImpl generalService) {
        this.generalService = generalService;
    }

    @PostMapping("/getApiKey")
    public ResponseEntity<String> getApiKey(@RequestBody ApiKeyRequest request) {
        try {
            String apiKey = generalService.getApiKey(request.getCompanyName());
            return ResponseEntity.ok(apiKey);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get API key", e);
        }
    }

    @PostMapping("/createUser")
    public ResponseEntity<Long> createUser(@RequestBody CreateUserRequest request) {
        try {
            Long userId = generalService.createUser(request.getLogin(), request.getApiKey());
            return ResponseEntity.ok(userId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user", e);
        }
    }

    @PostMapping("/search")
    public ResponseEntity<ProductResponse> search(@RequestBody SearchRequest request) {
        try {
            ProductResponse response = generalService.search(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process search request", e);
        }
    }
}
