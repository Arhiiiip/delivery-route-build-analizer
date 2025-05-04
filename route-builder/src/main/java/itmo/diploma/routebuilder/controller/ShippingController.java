package itmo.diploma.routebuilder.controller;

import itmo.diploma.routebuilder.dto.request.ShippingRequest;
import itmo.diploma.routebuilder.dto.response.ShippingResponse;
import itmo.diploma.routebuilder.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipping")
public class ShippingController {

    @Autowired
    private ShippingService shippingService;

    @PostMapping("/calculate")
    public List<ShippingResponse> calculateShipping(@RequestBody ShippingRequest request) {
        List<ShippingResponse> response = shippingService.findAllOffers(request);
        return response;
    }
}
