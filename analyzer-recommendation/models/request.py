from pydantic import BaseModel
from typing import List
from models.entities import ProductWithRoutes


class ProductRequest(BaseModel):
    user_requirement: str
    productsWithRoutes: List[ProductWithRoutes]