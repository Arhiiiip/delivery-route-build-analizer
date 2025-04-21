from pydantic import BaseModel

class Product(BaseModel):
    name: str
    price: float
    origin_country: str
    destination_country: str