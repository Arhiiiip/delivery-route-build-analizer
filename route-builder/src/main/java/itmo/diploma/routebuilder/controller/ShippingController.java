package itmo.diploma.routebuilder.controller;

import itmo.diploma.routebuilder.dto.request.ShippingRequest;
import itmo.diploma.routebuilder.dto.response.ShippingResponse;
import itmo.diploma.routebuilder.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipping")
public class ShippingController {

    @Autowired
    private ShippingService shippingService;

    @PostMapping("/calculate")
    public ResponseEntity<ShippingResponse> calculateShipping(@RequestBody ShippingRequest request) {
        ShippingResponse response = shippingService.findBestOffer(request);
        return ResponseEntity.ok(response);
    }
}
