from fastapi import FastAPI

from models.request import ProductRequest
from models.response import ProductResponse
from services.recommendation_service import ProductRecommendationService

app = FastAPI()
service = ProductRecommendationService()

@app.post("/recommend", response_model=ProductResponse)
async def recommend(request: ProductRequest):
    return service.recommend_products(request)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8190)