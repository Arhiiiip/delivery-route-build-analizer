package itmo.diploma.analytics.controller;

import itmo.diploma.analytics.dto.request.ProductRequest;
import itmo.diploma.analytics.service.NeuralNetworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class AnalizController {

    private NeuralNetworkService neuralNetworkService;

    @Autowired
    public AnalizController(NeuralNetworkService neuralNetworkService) {
        this.neuralNetworkService = neuralNetworkService;
    }

    @PostMapping("/recommend")
    public ResponseEntity<ProductRequest> getRecommendations(@RequestBody ProductRequest request) {
        try {
            ProductRequest response = neuralNetworkService.analyzeProducts(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
