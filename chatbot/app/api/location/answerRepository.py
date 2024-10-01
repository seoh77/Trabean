import requests
from typing import List, Dict
from dotenv import load_dotenv
import os
import json
from .answerSchemas import Place, TravelRequest, TravelResponse, Address
from .googleRepository import GoogleAPI


# 환경 변수 로드
load_dotenv()
GOOGLE_API_KEY = os.getenv("GOOGLE_API_KEY")

# json 읽어오기
current_directory = os.path.dirname(os.path.abspath(__file__))  # 현재 파일 위치
project_root = os.path.abspath(os.path.join(current_directory, "../../../"))  # 최상위 디렉토리로 이동

# radiusList = {
#   "한국": 5000,
#   "미국": 12000,
#   "독일": 8000,
#   "프랑스": 8500,
#   "일본": 6000,
#   "중국": 15000,
#   "영국": 7000,
#   "스위스": 4000,
#   "캐나다": 10000
# }


# 적당한 2N개의 관광, 식당 목록 반환
class PlaceFetcher:
    def __init__(self):
        self.googleAPI = GoogleAPI()
       
    def readJson(self, fileName):
        file_path = os.path.join(project_root, "data", f"{fileName}.json")
        with open(file_path, "r", encoding="utf-8") as file:
            return json.load(file)
        
    
    # 여행 반경 반환
    def getRadius(self, trans: str):
        radius = self.readJson("transRadiusList")[trans]
        return radius
        
        
    # id 기반으로 정보 받아서 넘겨주기
    async def getPlaceInfo(self, placeId):
        fields = "displayName,googleMapsUri,location,rating,userRatingCount,primaryType,goodForChildren,paymentOptions,formattedAddress,editorialSummary"
        info = self.googleAPI.searchDetail(placeId, fields)
        return info

    
    async def getLogingLists(self, lat, lon, radius):
        hotelLists = await self.googleAPI.searchNearby(lat, lon, radius, 20, ["hotel"])
        return hotelLists


    def getLogingTypes(self, preferLoging):
        logingTypes = self.readJson("logingList")[preferLoging]
        return logingTypes


    #  사용자 관심사에 맞는 장소 리스트를 가져오기
    def fetchRecommendedPlaces(self, requestBody, hotel) -> List[Place]:
        self.recommandPlaces["location"] = requestBody.location
        self.recommandPlaces["accomodation"] = hotel
        self.recommandPlaces["places"]["attraction"] = self.attractionData #사용자 관심사에 맞는 장소 목록 2*(days-1)개 이상, 10개 이하. day = 1이라면, >= 1
        self.recommandPlaces["places"]["restaurant"] = [] #사용자 관심사에 맞는 식당 목록 2*(days-1)개 이상, 10개 이하. day = 1이라면 >= 1개
        
        #관심사에 따라 place search

        return self.recommandPlaces
          
    
    async def getPlaces(self, requestBody: TravelRequest) -> TravelResponse:
        self.attractionData = await self.getAttractions( requestBody.attractions) #관광지 id기반으로 정보 받아옴
        midLocation = self.getLocation(self.attractionData) #관광지들의 중심 좌표 받아옴
        print(midLocation)
        hotel = await self.getHotelLocation(midLocation["midLat"], midLocation["midLon"], requestBody.transportation, requestBody.preferences.priority) # 관광지 근처 호텔 정보 받아옴
        return self.fetchRecommendedPlaces(requestBody, hotel)


# 이동 수단에 따른 반경 목록
radiusList = {
  "도보": 800,
  "자전거": 2000,
  "자동차": 10000,
  "대중교통": 15000,
  "휠체어": 600
}