from typing import List
from .answerSchemas import TravelRequest, TravelResponse
from .answerRepository import PlaceFetcher
import itertools


class ChatBotAnswer:
    def __init__(self):
        self.placeFetcher = PlaceFetcher()

    # DP 알고리즘을 사용하여 주어진 일수(days)에 맞는 최적의 여행 경로를 생성.
    async def getTravelRoutes(self, request: TravelRequest) -> TravelResponse:
        placesData = await self.placeFetcher.getPlaces() #여행 장소 목록
        
        # 장소 목록을 가공하여 계산을 위한 데이터 얻기
        # "위도, 경로, 이름"
        
        # 장소 목록을 가공하여 거리 데이터 얻기
        # H : hotel
        # A : attraction
        # R : restaurant
        distHToA = self.getDist()
        distRToH = self.getDist2()
        distAToR = self.getDist()
        
        optimalRoute = {}

        return optimalRoute
