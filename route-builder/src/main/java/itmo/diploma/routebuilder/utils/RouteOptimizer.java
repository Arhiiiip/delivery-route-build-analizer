package itmo.diploma.routebuilder.utils;

import itmo.diploma.routebuilder.entity.Route;

import java.util.Comparator;
import java.util.List;

public class RouteOptimizer {

    public Route findOptimalRoute(List<Route> routes) {
        return routes.stream()
                .min(Comparator.comparing(Route::getCost))
                .orElseThrow(() -> new RuntimeException("Маршруты не найдены"));
    }
}
