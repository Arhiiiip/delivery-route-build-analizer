package itmo.diploma.routebuilder.entity;

public record RouteSegment(
        String fromCountry,
        String toCountry,
        String provider,
        double price,
        int time
) {}