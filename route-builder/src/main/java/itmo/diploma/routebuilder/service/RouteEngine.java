package itmo.diploma.routebuilder.service;

import itmo.diploma.routebuilder.dto.request.DeliveryRequest;
import itmo.diploma.routebuilder.dto.response.DeliveryResponse;
import itmo.diploma.routebuilder.entity.LogisticsProvider;
import itmo.diploma.routebuilder.entity.Priority;
import itmo.diploma.routebuilder.entity.RouteNode;
import itmo.diploma.routebuilder.entity.RouteSegment;
import itmo.diploma.routebuilder.exception.RouteNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RouteEngine {
    private final List<LogisticsProvider> providers;

    public RouteEngine(List<LogisticsProvider> providers) {
        this.providers = providers;
    }

    public DeliveryResponse findOptimalRoute(DeliveryRequest request) {
        PriorityQueue<RouteNode> queue = new PriorityQueue<>();
        Map<String, RouteNode> bestNodes = new HashMap<>();

        RouteNode startNode = new RouteNode(
                request.fromCountry(),
                null,
                0.0,
                0,
                null
        );

        queue.add(startNode);
        bestNodes.put(request.fromCountry(), startNode);

        while (!queue.isEmpty()) {
            RouteNode current = queue.poll();

            if (current.country().equals(request.toCountry())) {
                return buildResponse(current);
            }

            getAvailableSegments(current.country()).forEach(segment -> {
                RouteNode newNode = createNewNode(current, segment, request.priority());

                if (!bestNodes.containsKey(newNode.country()) ||
                        newNode.totalCost() < bestNodes.get(newNode.country()).totalCost()) {

                    bestNodes.put(newNode.country(), newNode);
                    queue.add(newNode);
                }
            });
        }
        throw new RouteNotFoundException("No route found");
    }

    private List<RouteSegment> getAvailableSegments(String fromCountry) {
        List<RouteSegment> segments = new ArrayList<>();
        providers.forEach(provider ->
                segments.addAll(provider.getAvailableRoutes(fromCountry))
        );
        return segments;
    }

    private RouteNode createNewNode(RouteNode current, RouteSegment segment, Priority priority) {
        double cost = priority == Priority.PRICE ?
                current.totalCost() + segment.price() :
                current.totalCost();

        int time = priority == Priority.TIME ?
                current.totalTime() + segment.time() :
                current.totalTime();

        return new RouteNode(
                segment.toCountry(),
                current,
                cost,
                time,
                segment.provider()
        );
    }

    private DeliveryResponse buildResponse(RouteNode node) {
        LinkedList<String> route = new LinkedList<>();
        LinkedList<String> providers = new LinkedList<>();
        double totalPrice = 0;
        int totalTime = 0;

        while (node != null) {
            route.addFirst(node.country());
            if (node.lastProvider() != null) {
                providers.addFirst(node.lastProvider());
                totalPrice += getSegmentPrice(node);
                totalTime += getSegmentTime(node);
            }
            node = node.previous();
        }

        return new DeliveryResponse(
                route,
                totalPrice,
                totalTime,
                String.join(" → ", providers)
        );
    }

    private double getSegmentPrice(RouteNode node) {
        return node.previous() == null ? 0 :
                node.totalCost() - node.previous().totalCost();
    }

    private int getSegmentTime(RouteNode node) {
        return node.previous() == null ? 0 :
                node.totalTime() - node.previous().totalTime();
    }
}
