from pydantic import BaseModel
from typing import List
from .entities import Product

class ProductRecommendation(BaseModel):
    product: Product
    delivery_route: List[str]
    estimated_cost: float
    estimated_days: int
    recommendation_reason: str
    score: float

class ProductResponse(BaseModel):
    recommended_product: Product
    delivery_route: List[str]
    estimated_cost: float
    estimated_days: int
    recommendation_reason: str
    recommendations: List[ProductRecommendation]