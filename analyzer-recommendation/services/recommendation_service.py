import numpy as np
from transformers import AutoModel, AutoTokenizer
import torch
from typing import List

from models.entities import ProductWithRoutes, Currency
from models.request import ProductRequest
from models.response import ProductResponse, ProductRecommendation

class ProductRecommendationService:
    def __init__(self):
        model_name = "sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2"
        self.tokenizer = AutoTokenizer.from_pretrained(model_name)
        self.model = AutoModel.from_pretrained(model_name)
        self.embedding_size = 384
        self.max_cost = 10000.0
        self.max_hours = 336.0

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
        for product_with_routes in request.productsWithRoutes:
            product_text = self.generate_product_text(product_with_routes)
            product_embedding = self.get_text_embedding(product_text)
            text_similarity = self.cosine_similarity(user_embedding, product_embedding)

            cost_usd = self.convert_to_usd(product_with_routes.product)
            hours = product_with_routes.hoursForDelivery
            normalized_cost = cost_usd / self.max_cost
            normalized_hours = hours / self.max_hours

            final_score = self.calculate_final_score(
                text_similarity,
                normalized_cost,
                normalized_hours,
                priority
            )

            print(f"Product: {product_with_routes.product.name}, Cost: {cost_usd:.2f} USD, "
                  f"Text Similarity: {text_similarity:.4f}, Normalized Cost: {normalized_cost:.4f}, "
                  f"Normalized Hours: {normalized_hours:.4f}, Final Score: {final_score:.4f}")

            scored_products.append(ProductScore(
                product_with_routes,
                final_score,
                text_similarity,
                cost_usd,
                hours
            ))

        scored_products.sort(key=lambda x: x.final_score, reverse=True)
        best_product = scored_products[0]
        return self.build_response(best_product, scored_products, priority)

    def convert_to_usd(self, product: 'Product') -> float:
        price = float(product.price.replace(',', '.'))
        return price

    def determine_priority(self, text: str) -> str:
        price_score = sum(1 for word in self.price_keywords if word in text)
        speed_score = sum(1 for word in self.speed_keywords if word in text)

        if price_score > speed_score:
            return "price"
        elif speed_score > price_score:
            return "speed"
        return "balanced"

    def calculate_final_score(self, text_similarity: float, cost: float, hours: float, priority: str) -> float:
        if priority == "price":
            return (text_similarity * 0.3) + ((1 - cost) * 0.6) + ((1 - hours) * 0.1)
        elif priority == "speed":
            return (text_similarity * 0.3) + ((1 - hours) * 0.6) + ((1 - cost) * 0.1)
        return (text_similarity * 0.6) + ((1 - cost) * 0.2) + ((1 - hours) * 0.2)

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
            delivery_route=best_product.product.countryRoute,
            estimated_cost=best_product.cost,
            estimated_days=int(best_product.hours / 24),
            recommendation_reason=f"Лучшее соответствие ({priority_text}, текстовая схожесть: {best_product.text_similarity:.0%})",
            recommendations=[]
        )

        for product_score in all_products[1:4]:
            response.recommendations.append(self.create_recommendation(product_score, priority))

        return response

    def create_recommendation(self, product_score: 'ProductScore', priority: str) -> ProductRecommendation:
        priority_text = {
            "price": "с упором на цену",
            "speed": "с упором на скорость",
            "balanced": "сбалансированный"
        }.get(priority, "сбалансированный")

        return ProductRecommendation(
            product=product_score.product,
            delivery_route=product_score.product.countryRoute,
            estimated_cost=product_score.cost,
            estimated_days=int(product_score.hours / 24),
            recommendation_reason=f"Альтернатива ({priority_text}, текстовая схожесть: {product_score.text_similarity:.0%})",
            score=product_score.final_score
        )

    def generate_product_text(self, product_with_routes: ProductWithRoutes) -> str:
        product = product_with_routes.product
        return (f"{product.name} {product.price} {product.currency.name} "
                f"{product.countryFrom} {product.countryTo} "
                f"{product_with_routes.logisticCompanyName}")

class ProductScore:
    def __init__(self, product: ProductWithRoutes, final_score: float,
                 text_similarity: float, cost: float, hours: int):
        self.product = product
        self.final_score = final_score
        self.text_similarity = text_similarity
        self.cost = cost
        self.hours = hours