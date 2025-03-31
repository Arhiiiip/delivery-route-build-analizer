package itmo.diploma.analytics.service;

import itmo.diploma.analytics.dto.request.ProductRequest;
import itmo.diploma.analytics.dto.response.ProductResponse;
import itmo.diploma.analytics.entity.Product;
import itmo.diploma.analytics.entity.ProductRecommendation;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductRecommendationService {
    private final MultiLayerNetwork model;
    private final int embeddingSize = 384;

    public ProductRecommendationService() throws Exception {
        this.model = KerasModelImport.importKerasSequentialModelAndWeights("paraphrase-multilingual-MiniLM-L12-v2.onnx.zip");
    }

    public ProductResponse recommendProducts(ProductRequest request) {
        INDArray userEmbedding = getTextEmbedding(request.getUserRequirement());

        List<ProductScore> scoredProducts = request.getProducts().stream()
                .map(product -> {
                    INDArray productEmbedding = getTextEmbedding(generateProductText(product));
                    double similarity = cosineSimilarity(userEmbedding, productEmbedding);
                    return new ProductScore(product, similarity);
                })
                .sorted(Comparator.comparingDouble(ProductScore::getScore).reversed())
                .collect(Collectors.toList());

        ProductScore bestProduct = scoredProducts.get(0);
        return buildResponse(bestProduct, scoredProducts);
    }

    private INDArray getTextEmbedding(String text) {
        float[] fakeEmbedding = new float[embeddingSize];
        Arrays.fill(fakeEmbedding, 0.1f);
        return Nd4j.create(fakeEmbedding);
    }

    private double cosineSimilarity(INDArray vecA, INDArray vecB) {
        return vecA.mul(vecB).sumNumber().doubleValue() /
                (vecA.norm2Number().doubleValue() * vecB.norm2Number().doubleValue());
    }

    private ProductResponse buildResponse(ProductScore bestProduct, List<ProductScore> allProducts) {
        ProductResponse response = new ProductResponse();
        response.setRecommendedProduct(bestProduct.getProduct());
        response.setDeliveryRoute(calculateRoute(bestProduct.getProduct()));
        response.setEstimatedCost(calculateCost(bestProduct.getProduct()));
        response.setEstimatedDays(calculateDays(bestProduct.getProduct()));
        response.setRecommendationReason(String.format("Best match (%.0f%%)", bestProduct.getScore() * 100));

        response.setRecommendations(allProducts.stream()
                .skip(1)
                .limit(3)
                .map(this::createRecommendation)
                .collect(Collectors.toList()));

        return response;
    }

    private List<String> calculateRoute(Product product) {
        return Arrays.asList(
                product.getOriginCountry(),
                "International Hub",
                product.getDestinationCountry()
        );
    }

    private double calculateCost(Product product) {
        return product.getPrice() * 0.1;
    }

    private int calculateDays(Product product) {
        return product.getOriginCountry().equals(product.getDestinationCountry()) ? 3 : 7;
    }

    private ProductRecommendation createRecommendation(ProductScore productScore) {
        ProductRecommendation rec = new ProductRecommendation();
        rec.setDeliveryRoute(calculateRoute(productScore.getProduct()));
        rec.setEstimatedCost(calculateCost(productScore.getProduct()));
        rec.setEstimatedDays(calculateDays(productScore.getProduct()));
        rec.setRecommendationReason(String.format("Alternative (%.0f%%)", productScore.getScore() * 100));
        rec.setScore(productScore.getScore());
        return rec;
    }

    private String generateProductText(Product product) {
        return String.format("%s %.2f %s %s",
                product.getName(),
                product.getPrice(),
                product.getOriginCountry(),
                product.getDestinationCountry());
    }

    private static class ProductScore {
        private final Product product;
        private final double score;

        public ProductScore(Product product, double score) {
            this.product = product;
            this.score = score;
        }

        public Product getProduct() {
            return product;
        }

        public double getScore() {
            return score;
        }
    }
}