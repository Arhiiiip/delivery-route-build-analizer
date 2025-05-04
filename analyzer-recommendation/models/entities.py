from enum import Enum

import long
from pydantic import BaseModel

class Currency(Enum):
    RUB = 1
    USD = 2
    EUR = 3


class Product(BaseModel):
    name: str
    price: str
    currency: Currency
    url: str
    countryFrom: str
    countryTo: str
    weight: long

class ProductWithRoutes(BaseModel):
    product: Product
    countryRoute: list[str]
    cost: long
    logisticCompanyName: str
    hoursForDelivery: long

