package itmo.diploma.research.controller;

import itmo.diploma.research.dto.request.ProductSearchRequest;
import itmo.diploma.research.entity.ProductInfo;
import itmo.diploma.research.service.ProductSearchService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductSearchService productSearchService;

    public ProductController(ProductSearchService productSearchService) {
        this.productSearchService = productSearchService;
    }

    @PostMapping("/search")
    public List<ProductInfo> searchProducts(@RequestBody ProductSearchRequest request) {
        return productSearchService.searchProducts(request);
    }
}