package itmo.diploma.routebuilder.dto.request;

import itmo.diploma.routebuilder.entity.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record DeliveryRequest(
        @NotBlank String fromCountry,
        @NotBlank String toCountry,
        @Positive double weight,
        Priority priority
) {}
