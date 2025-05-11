package itmo.diploma.routebuilder.dto.response;

import java.util.List;

public record DeliveryResponse(
        List<String> route,
        double totalPrice,
        int totalTime,
        String providers
) {}