package itmo.diploma.analytics.controller;

import itmo.diploma.analytics.dto.request.ProductRequest;
import itmo.diploma.analytics.dto.response.ProductResponse;
import itmo.diploma.analytics.service.ProductRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class AnalizController {

    private ProductRecommendationService productRecommendationService;

    @Autowired
    public AnalizController(ProductRecommendationService productRecommendationService) {
        this.productRecommendationService = productRecommendationService;
    }

    @PostMapping("/recommend")
    public ResponseEntity<ProductResponse> getRecommendations(@RequestBody ProductRequest request) {
        try {
            ProductResponse response = productRecommendationService.recommendProducts(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}