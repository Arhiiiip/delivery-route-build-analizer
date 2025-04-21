from pydantic import BaseModel
from typing import List
from .entities import Product

class ProductRequest(BaseModel):
    user_requirement: str
    products: List[Product]