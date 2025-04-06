package itmo.diploma.research.controller;

import itmo.diploma.research.dto.request.ProductSearchRequest;
import itmo.diploma.research.entity.Product;
import itmo.diploma.research.service.ProductSearchService;
import org.json.JSONException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductSearchService productSearchService;

    public ProductController(ProductSearchService productSearchService) {
        this.productSearchService = productSearchService;
    }

    @PostMapping("/search")
    public List<Product> searchProducts(@RequestBody ProductSearchRequest request) throws IOException, InterruptedException {
        return productSearchService.searchProducts(request);
    }
}