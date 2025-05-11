package itmo.diploma.routebuilder.entity;

import java.util.List;

public interface LogisticsProvider {
    List<RouteSegment> getAvailableRoutes(String fromCountry);
    String getName();
}
