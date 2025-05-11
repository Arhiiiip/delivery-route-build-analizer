package itmo.diploma.routebuilder.controller;

import itmo.diploma.routebuilder.dto.request.DeliveryRequest;
import itmo.diploma.routebuilder.dto.response.DeliveryResponse;
import itmo.diploma.routebuilder.service.RouteEngine;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/delivery")
public class DeliveryController {

    private final RouteEngine routeEngine;

    public DeliveryController(RouteEngine routeEngine) {
        this.routeEngine = routeEngine;
    }

    @GetMapping
    public ResponseEntity<DeliveryResponse> calculateRoute(@Valid @RequestBody DeliveryRequest request) {
        DeliveryResponse response = routeEngine.findOptimalRoute(request);
        return ResponseEntity.ok(response);
    }
}
