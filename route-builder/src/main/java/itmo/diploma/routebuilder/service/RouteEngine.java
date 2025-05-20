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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Service
public class RouteEngine {
    private final List<LogisticsProvider> providers;

    public RouteEngine(List<LogisticsProvider> providers) {
        this.providers = providers;
    }

    public DeliveryResponse findOptimalRoute(DeliveryRequest request) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(providers.size());
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
                executor.shutdown();
                return buildResponse(current);
            }
            Future<List<RouteSegment>> futureSegments = executor.submit(() ->
                    getAvailableSegments(current.country())
            );
            List<RouteSegment> segments = futureSegments.get();
            processSegments(current, segments, request, queue, bestNodes);
        }

        executor.shutdown();
        throw new RouteNotFoundException("No route found");
    }

    private List<RouteSegment> getAvailableSegments(String fromCountry) {
        List<RouteSegment> segments = new ArrayList<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (LogisticsProvider provider : providers) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                segments.addAll(provider.getAvailableRoutes(fromCountry));
            });
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return segments;
    }

    private void processSegments(RouteNode current,
                                 List<RouteSegment> segments,
                                 DeliveryRequest request,
                                 PriorityQueue<RouteNode> queue,
                                 Map<String, RouteNode> bestNodes) {
        segments.forEach(segment -> {
            RouteNode newNode = createNewNode(current, segment, request.priority());
            if (!bestNodes.containsKey(newNode.country()) ||
                    newNode.totalCost() < bestNodes.get(newNode.country()).totalCost()) {
                bestNodes.put(newNode.country(), newNode);
                queue.add(newNode);
            }
        });
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
