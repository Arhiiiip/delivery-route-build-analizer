import numpy as np
from transformers import AutoModel, AutoTokenizer
import torch
from typing import List

from models.entities import Product
from models.request import ProductRequest
from models.response import ProductResponse, ProductRecommendation

class ProductRecommendationService:
    def __init__(self):
        model_name = "sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2"
        self.tokenizer = AutoTokenizer.from_pretrained(model_name)
        self.model = AutoModel.from_pretrained(model_name)
        self.embedding_size = 384
        self.max_price = 10000.0
        self.max_days = 14.0

        self.price_keywords = {
            'дешевый', 'недорогой', 'цена', 'стоимость', 'бюджетный',
            'экономичный', 'доступный', 'низкая цена'
        }
        self.speed_keywords = {
            'быстрый', 'быстро', 'скорость', 'доставка', 'срочный',
            'экспресс', 'быстрая доставка'
        }

    def recommend_products(self, request: ProductRequest) -> ProductResponse:
        user_embedding = self.get_text_embedding(request.user_requirement)
        priority = self.determine_priority(request.user_requirement.lower())

        scored_products = []
        for product in request.products:
            product_text = self.generate_product_text(product)
            product_embedding = self.get_text_embedding(product_text)
            text_similarity = self.cosine_similarity(user_embedding, product_embedding)

            cost = self.calculate_cost(product)
            days = self.calculate_days(product)
            normalized_cost = cost / self.max_price
            normalized_days = days / self.max_days

            final_score = self.calculate_final_score(
                text_similarity,
                normalized_cost,
                normalized_days,
                priority
            )

            # Отладочный вывод
            print(f"Product: {product.name}, Price: {product.price}, Text Similarity: {text_similarity:.4f}, Normalized Cost: {normalized_cost:.4f}, Normalized Days: {normalized_days:.4f}, Final Score: {final_score:.4f}")

            scored_products.append(ProductScore(product, final_score, text_similarity, cost, days))

        scored_products.sort(key=lambda x: x.final_score, reverse=True)
        best_product = scored_products[0]
        return self.build_response(best_product, scored_products, priority)

    def determine_priority(self, text: str) -> str:
        price_score = sum(1 for word in self.price_keywords if word in text)
        speed_score = sum(1 for word in self.speed_keywords if word in text)

        if price_score > speed_score:
            return "price"
        elif speed_score > price_score:
            return "speed"
        else:
            return "balanced"

    def calculate_final_score(self, text_similarity: float, cost: float, days: float, priority: str) -> float:
        if priority == "price":
            # Увеличиваем вес цены до 0.6
            return (text_similarity * 0.3) + ((1 - cost) * 0.6) + ((1 - days) * 0.1)
        elif priority == "speed":
            return (text_similarity * 0.3) + ((1 - days) * 0.6) + ((1 - cost) * 0.1)
        else:
            return (text_similarity * 0.6) + ((1 - cost) * 0.2) + ((1 - days) * 0.2)

    def get_text_embedding(self, text: str) -> torch.Tensor:
        inputs = self.tokenizer(text, return_tensors="pt", padding=True, truncation=True)
        with torch.no_grad():
            outputs = self.model(**inputs)
        return outputs.last_hidden_state.mean(dim=1).squeeze()

    def cosine_similarity(self, vec_a: torch.Tensor, vec_b: torch.Tensor) -> float:
        return torch.dot(vec_a, vec_b) / (torch.norm(vec_a) * torch.norm(vec_b))

    def build_response(self, best_product: 'ProductScore', all_products: List['ProductScore'], priority: str) -> ProductResponse:
        priority_text = {
            "price": "оптимизировано по цене",
            "speed": "оптимизировано по скорости доставки",
            "balanced": "сбалансированная оптимизация"
        }.get(priority, "сбалансированная оптимизация")

        response = ProductResponse(
            recommended_product=best_product.product,
            delivery_route=self.calculate_route(best_product.product),
            estimated_cost=best_product.cost,
            estimated_days=best_product.days,
            recommendation_reason=f"Лучшее соответствие ({priority_text}, текстовая схожесть: {best_product.text_similarity:.0%})",
            recommendations=[]
        )

        for product_score in all_products[1:4]:
            response.recommendations.append(self.create_recommendation(product_score, priority))

        return response

    def calculate_route(self, product: Product) -> List[str]:
        return [product.origin_country, "Международный хаб", product.destination_country]

    def calculate_cost(self, product: Product) -> float:
        return product.price * 0.1

    def calculate_days(self, product: Product) -> int:
        return 3 if product.origin_country == product.destination_country else 7

    def create_recommendation(self, product_score: 'ProductScore', priority: str) -> ProductRecommendation:
        product = product_score.product
        priority_text = {
            "price": "с упором на цену",
            "speed": "с упором на скорость",
            "balanced": "сбалансированный"
        }.get(priority, "сбалансированный")

        return ProductRecommendation(
            product=product,
            delivery_route=self.calculate_route(product),
            estimated_cost=product_score.cost,
            estimated_days=product_score.days,
            recommendation_reason=f"Альтернатива ({priority_text}, текстовая схожесть: {product_score.text_similarity:.0%})",
            score=product_score.final_score
        )

    def generate_product_text(self, product: Product) -> str:
        return f"{product.name} {product.price:.2f} {product.origin_country} {product.destination_country}"

class ProductScore:
    def __init__(self, product: Product, final_score: float, text_similarity: float, cost: float, days: int):
        self.product = product
        self.final_score = final_score
        self.text_similarity = text_similarity
        self.cost = cost
        self.days = days