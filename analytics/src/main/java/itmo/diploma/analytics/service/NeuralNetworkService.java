package itmo.diploma.analytics.service;


import itmo.diploma.analytics.dto.request.ProductRequest;
import itmo.diploma.analytics.entity.Product;
import itmo.diploma.analytics.entity.ProductRecommendation;
import jakarta.annotation.PostConstruct;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NeuralNetworkService {
    private static final Logger logger = LoggerFactory.getLogger(NeuralNetworkService.class);

    private MultiLayerNetwork model;
    private final RestTemplate restTemplate;
    //todo дозаполнить
    @Value("${neural.network.model.path:/model.zip}")
    private String modelPath;

    @Value("${openroute.api.key:api-key}")
    private String apiKey;

    private final Map<String, List<String>> countryConnections = new HashMap<>();
    private static final double COST_PER_KM = 0.05;
    private static final int TOP_N = 10;

    public NeuralNetworkService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        try {
            File modelFile = new File(modelPath);
            if (!modelFile.exists()) {
                throw new IllegalStateException("Model file not found at: " + modelPath);
            }
            model = ModelSerializer.restoreMultiLayerNetwork(modelFile);
            logger.info("Neural network model loaded successfully from {}", modelPath);
            initializeCountryConnections();
        } catch (Exception e) {
            logger.error("Failed to initialize neural network model", e);
            throw new RuntimeException("Neural network initialization failed", e);
        }
    }

    public ProductRequest analyzeProducts(ProductRequest request) {
        validateRequest(request);
        List<ProductRecommendation> recommendations = generateRecommendations(request);
        request.setRecommendations(selectTopRecommendations(recommendations));
        return request;
    }

    private void validateRequest(ProductRequest request) {
        if (request == null || request.getProducts() == null || request.getProducts().isEmpty()) {
            throw new IllegalArgumentException("Product request cannot be null or empty");
        }
        if (request.getUserRequirement() == null || request.getUserRequirement().isEmpty()) {
            throw new IllegalArgumentException("User requirement cannot be empty");
        }
    }

    private List<ProductRecommendation> generateRecommendations(ProductRequest request) {
        INDArray input = prepareInputData(request);
        INDArray output = model.output(input);

        List<ProductRecommendation> recommendations = new ArrayList<>();
        for (int i = 0; i < request.getProducts().size(); i++) {
            Product product = request.getProducts().get(i);
            double score = output.getDouble(i);

            List<String> route = generateOptimalRoute(product.getSourceCountry(), request.getDestinationCountry());
            double distance = getRouteDistance(route);

            recommendations.add(ProductRecommendation.builder()
                    .product(product)
                    .deliveryRoute(route)
                    .estimatedCost(calculateTotalCost(product, distance))
                    .estimatedDays(calculateDeliveryTime(distance))
                    .recommendationReason(generateReason(product, request.getUserRequirement()))
                    .score(score)
                    .build());
        }
        return recommendations;
    }

    private List<ProductRecommendation> selectTopRecommendations(List<ProductRecommendation> recommendations) {
        return recommendations.stream()
                .sorted(Comparator.comparingDouble(ProductRecommendation::getScore).reversed())
                .limit(TOP_N)
                .collect(Collectors.toList());
    }

    private INDArray prepareInputData(ProductRequest request) {
        double[] features = new double[request.getProducts().size() * 3];
        int index = 0;

        for (Product product : request.getProducts()) {
            features[index++] = normalizePrice(product.getPrice());
            features[index++] = calculateDistance(product.getSourceCountry(), request.getDestinationCountry());
            features[index++] = encodeRequirement(request.getUserRequirement());
        }

        return Nd4j.create(features).reshape(1, features.length);
    }

    private double getRouteDistance(List<String> route) {
        if (route.size() < 2) return 0.0;

        double totalDistance = 0.0;
        for (int i = 0; i < route.size() - 1; i++) {
            totalDistance += fetchDistanceFromApi(route.get(i), route.get(i + 1));
        }
        return totalDistance;
    }

    private double fetchDistanceFromApi(String origin, String destination) {
        String url = String.format(
                "https://api.openrouteservice.org/v2/directions/driving-car?api_key=%s&start=%s&end=%s",
                apiKey, getCoordinates(origin), getCoordinates(destination)
        );

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("features")) {
                List<Map<String, Object>> features = (List<Map<String, Object>>) response.get("features");
                Map<String, Object> properties = (Map<String, Object>) features.get(0).get("properties");
                Map<String, Object> segments = (Map<String, Object>) ((List<?>) properties.get("segments")).get(0);
                return (double) segments.get("distance") / 1000;
            }
        } catch (Exception e) {
            logger.warn("Failed to fetch distance for {} to {}, using fallback", origin, destination, e);
        }
        return calculateFallbackDistance(origin, destination);
    }

    private String getCoordinates(String country) {
        Map<String, String> countryCoords = new HashMap<>();
        countryCoords.put("USA", "-77.0369,38.9072");
        countryCoords.put("Germany", "13.4050,52.5200");
        countryCoords.put("Russia", "37.6173,55.7558");
        countryCoords.put("China", "116.4074,39.9042");
        return countryCoords.getOrDefault(country, "0,0");
    }

    private double calculateTotalCost(Product product, double distance) {
        return product.getPrice() + (distance * COST_PER_KM);
    }

    private int calculateDeliveryTime(double distance) {
        return (int) Math.ceil(distance / 1000.0) + 1;
    }

    private double calculateFallbackDistance(String from, String to) {
        return countryConnections.get(from).contains(to) ? 1000.0 : 5000.0;
    }

    private void initializeCountryConnections() {
        countryConnections.put("USA", Arrays.asList("Germany", "UK"));
        countryConnections.put("Germany", Arrays.asList("USA", "Russia"));
        countryConnections.put("China", Arrays.asList("Japan", "Russia"));
        countryConnections.put("Russia", Arrays.asList("Germany", "China"));
    }

    private double normalizePrice(double price) {
        return Math.min(Math.max(price / 10000.0, 0.0), 1.0);
    }

    private double calculateDistance(String from, String to) {
        return countryConnections.get(from).contains(to) ? 0.1 : 0.5;
    }

    private double encodeRequirement(String requirement) {
        return requirement.length() > 20 ? 0.8 : 0.4;
    }

    private List<String> generateOptimalRoute(String from, String to) {
        List<String> route = new ArrayList<>();
        route.add(from);
        if (countryConnections.get(from).contains(to)) {
            route.add(to);
            return route;
        }
        String transit = findTransitCountry(from, to);
        if (transit != null) route.add(transit);
        route.add(to);
        return route;
    }

    private String findTransitCountry(String from, String to) {
        return countryConnections.get(from).stream()
                .filter(country -> countryConnections.get(country).contains(to))
                .findFirst()
                .orElse(null);
    }

    private String generateReason(Product product, String requirement) {
        return String.format("Optimal price (%.2f) and delivery from %s", product.getPrice(), product.getSourceCountry());
    }
}
