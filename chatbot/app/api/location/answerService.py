from typing import List
from .answerSchemas import TravelRequest, TravelResponse
from .answerRepository import PlaceFetcher
import itertools


class ChatBotAnswer:
    def __init__(self):
        self.placeFetcher = PlaceFetcher()
        self.restaurant = []
        self.tourism = []
        
    # 장소 목록을 기반으로 위/경도 평균 구하기
    def getMidLocation(self, places):
        lat = [place["location"]["latitude"] for place in places]
        lon = [place["location"]["longitude"] for place in places]

        averageLatitude = sum(lat) / len(lat)
        averageLongitude = sum(lon) / len(lon)
        average = {
            "midLat" : averageLatitude, 
            "midLon" : averageLongitude
        }
        return average
    
    
    # 관광지 id 목록 받아서 관광지 목록에 대한 세부 정보들 반환
    async def getPlacesInfo(self, places):
        placesInfo = []
        
        for id in places:
            info = await self.placeFetcher.getPlaceInfo(id)
            if info :
                placesInfo.append(info)
            
        return placesInfo
    
    
    # 이동수단, 선호도에 맞는 알맞은 radius 반환
    def getOptimalRadius(self, trans, priority):
        radius = self.placeFetcher.getRadius(trans)
        optimalRadius = radius*priority[2] # 검색 반경
        return optimalRadius

    
    # 위/경도를 기준으로 반경 radius안에서 priority에 맞는 숙박시설 찾기
    async def getLoging(self, lat, lon, radius, priority, preferLoging):
        if radius > 50000 : radius = 50000
        types = self.placeFetcher.getLogingTypes(preferLoging)
        logingLists = await self.placeFetcher.getLogingLists(lat, lon, radius, types)
        
        if not logingLists : 
            logingLists = await self.placeFetcher.getLogingLists(lat, lon, radius, ["lodging"])
        
        if priority[0] > priority[1] : 
            hotel = sorted(logingLists["places"], key=lambda x: (-x["rating"], -x["userRatingCount"]))[0]
        else :
            hotel = sorted(logingLists["places"], key=lambda x: (-x["userRatingCount"], -x["rating"]))[0]
        return hotel
    
    
    async def getPlaces(self, lat, lon, radius, num, priority, interests):
        
        for interest in interests:
            pass
            
        

    # 주어진 일수(days)에 맞는 최적의 여행 경로를 생성하여 반환
    async def getTravelRoutes(self, request: TravelRequest) -> TravelResponse:
        self.tourism  = await self.getPlacesInfo(request.attractions)
        midLocation = self.getMidLocation(self.tourism) # center 위치 받기
        
        priority = request.preferences.priority
        transportation = request.transportation
        radius = self.getOptimalRadius(transportation, priority)
        preferLoging = request.preferences.preferLoging
        hotel = await self.getLoging(midLocation["midLat"], midLocation["midLon"], radius, priority, preferLoging) # 호텔 선정
        
        days = request.days
        interests = request.preferences.interest

        num = 2*days - len(self.tourism)
        places = self.getPlaces(hotel["location"]["lat"], hotel["location"]["lon"], radius, num, priority, interests) #장소 선정
        self.tourism += places
        
        # 장소 목록 나머지 받아오기
        
        # 식당 목록 나머지 받아오기
        
        
        placesData = await self.placeFetcher.getPlaces(request) #days일 간의 여행 장소 목록 (관광지 + 식당) 받아오기
        
        return placesData
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
