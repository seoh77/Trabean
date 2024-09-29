from pydantic import BaseModel
from typing import List, Dict


# 요청 바디에 대한 데이터 모델 정의
class Location(BaseModel):
    country: str  # 여행할 국가명
    city: str  # 여행할 도시명


class Preferences(BaseModel):
    interest: List[str]  # 여행 스타일
    priority: str  # 우선순위


class TravelRequest(BaseModel):
    location: Location  # 여행지 정보
    days: int  # 여행 기간
    transportation: str  # 이동수단
    preferences: Preferences  # 여행 선호 정보
    attraction : List[str] # 선호하는 관광지 목록


# 응답 바디에 대한 데이터 모델 정의
class Address(BaseModel):
    address: str  # 주소
    lat: float  # 위도
    lon: float  # 경도


class Review(BaseModel):
    rating: float  # 평점
    count: int  # 리뷰 수


class Info(BaseModel):
    placeId: str  # google place id
    imageId: str  # google image id
    mapUri: str  # google MAP URI


class Place(BaseModel):  # 하나의 일정
    name: str  # 방문 장소명
    category: str  # 카테고리
    address: Address  # 위치 정보
    review: Review  # 리뷰 정보
    info: Info  # google API 접근을 위한 정보


class TravelResponse(BaseModel):
    location: Location  # 여행지 기본 정보
    route: Dict[int, List[Place]]  # 일자, 하루 일정 목록
