package itmo.diploma.routebuilder.provider;

import itmo.diploma.routebuilder.entity.LogisticsProvider;
import itmo.diploma.routebuilder.entity.RouteSegment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DHLProvider implements LogisticsProvider {
    @Override
    public List<RouteSegment> getAvailableRoutes(String from) {
        return List.of(
                new RouteSegment(from, "DE", "DHL", 150, 48),
                new RouteSegment(from, "PL", "DHL", 80, 24),
                new RouteSegment(from, "CN", "DHL", 300, 72)
        );
    }

    @Override
    public String getName() {
        return "DHL";
    }
}
