package itmo.diploma.routebuilder.entity;

public record RouteNode(
        String country,
        RouteNode previous,
        double totalCost,
        int totalTime,
        String lastProvider
) implements Comparable<RouteNode> {

    @Override
    public int compareTo(RouteNode other) {
        return Double.compare(this.totalCost, other.totalCost);
    }
}