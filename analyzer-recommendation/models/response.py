from pydantic import BaseModel
from typing import List
from models.entities import ProductWithRoutes


class ProductRecommendation(BaseModel):
    product: ProductWithRoutes
    delivery_route: List[str]
    estimated_cost: float
    estimated_days: int
    recommendation_reason: str
    score: float

class ProductResponse(BaseModel):
    recommended_product: ProductWithRoutes
    delivery_route: List[str]
    estimated_cost: float
    estimated_days: int
    recommendation_reason: str
    recommendations: List[ProductRecommendation]