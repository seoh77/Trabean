from fastapi import FastAPI, Request
from pydantic import BaseModel
from typing import List, Optional, Dict

# FastAPI 애플리케이션 인스턴스 생성
location_answer_router = FastAPI()

# 요청 바디에 대한 데이터 모델 정의
class Location(BaseModel):
    country: str  # 여행할 국가명
    city: str     # 여행할 도시명

class Preferences(BaseModel):
    interest: List[str]  # 여행 스타일
    budget: int          # 여행 예산 (원화)
    Priority: str        # 우선순위

class TravelRequest(BaseModel):
    location: Location         # 여행지 정보
    date: str                  # 여행 기간
    Companions: str            # 동행인
    preferences: Preferences   # 여행 선호 정보

# POST 요청에 대한 엔드포인트 정의
@location_answer_router.post("/recommendLocation")
async def recommend_location(travel_request: TravelRequest):
    # 요청된 데이터를 출력
    print(travel_request)
    # 요청 데이터를 그대로 반환
    return travel_request