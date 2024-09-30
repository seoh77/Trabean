import requests
from typing import List, Dict
from dotenv import load_dotenv
import os
import json
from .answerSchemas import Place, TravelRequest, TravelResponse, Address

# 환경 변수 로드
load_dotenv()
GOOGLE_API_KEY = os.getenv("GOOGLE_API_KEY")

base_dir = os.path.dirname(os.path.abspath(__file__))
file_path = os.path.join(base_dir, "locationList.json")
with open(file_path, "r", encoding="utf-8") as file:
    locationData = json.load(file)

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
        self.locationData = locationData
        self.attractionData = []
        self.recommandPlaces = {}
        
    # 관광지 id 기반으로 정보 받아서 넘겨주기
    async def getAttractions(self, attractionId):
        attractions = []
        fields = "displayName,googleMapsUri,location,rating,userRatingCount,primaryType,goodForChildren,paymentOptions,formattedAddress,editorialSummary"
        # 각 ID에 대해 GET 요청 보내서 관광지 데이터 받아오기
        for id in attractionId:
            url = f"https://places.googleapis.com/v1/places/{id}?fields={fields}&key={GOOGLE_API_KEY}&languageCode=ko"
            response = requests.get(url)

            if response.status_code == 200:
                attractions.append(response.json())
            else:
                print(f"ID: {id} - Error: {response.status_code} - {response.text}")
                
        return attractions
            

    # 관광지 목록을 기반으로 위/경도 평균 구하기
    def getLocation(self, attractions):
        latitudes = [place["location"]["latitude"] for place in attractions]
        longitudes = [place["location"]["longitude"] for place in attractions]

        averageLatitude = sum(latitudes) / len(latitudes)
        averageLongitude = sum(longitudes) / len(longitudes)
        average = {
            "midLat" : averageLatitude, 
            "midLon" : averageLongitude
        }
        return average

    # 관광지의 위/경도를 기준으로 좋은 호텔 찾기
    async def getHotelLocation(self, lat, lon, transportation, priority):
        url = "https://places.googleapis.com/v1/places:searchNearby"
        headers = {
            "Content-Type": "application/json",  # JSON 형식으로 데이터 전송
            "X-Goog-Api-Key": GOOGLE_API_KEY,  # API KEY
            "X-Goog-FieldMask": "places.id,places.displayName,places.googleMapsUri,places.location,places.rating,places.userRatingCount,places.primaryType,places.goodForChildren,places.paymentOptions,places.photos.name",
        }

        # 요청 본문 
        radius = radiusList[transportation]*priority[2] # 검색 반경
        if radius > 50000 : radius = 50000
        
        data = {
            "includedTypes": ["hotel"],
            "languageCode": "ko",
            "maxResultCount": 20,
            "locationRestriction": {
                "circle": {
                    "center": {"latitude": lat, "longitude": lon},
                    "radius": radius,
                }
            },
        }

        response = requests.post(url, headers=headers, json=data)

        if response.status_code == 200:
            responseData = response.json()
            if responseData:  # 제대로 된 호텔을 찾음
                if priority[0] > priority[1] : 
                    hotelLocation  = sorted(responseData["places"], key=lambda x: (-x["rating"], -x["userRatingCount"]))[0]
                else :
                    hotelLocation  = sorted(responseData["places"], key=lambda x: (-x["userRatingCount"], -x["rating"]))[0]
                return hotelLocation
            else:
                print(f"No results found for radius {radius}. Increasing radius.")

        else:
            print("Error:", response.status_code, response.text)
            return


    #  사용자 관심사에 맞는 장소 리스트를 가져오기
    def fetchRecommendedPlaces(self, requestBody, hotel) -> List[Place]:
        self.recommandPlaces["location"] = requestBody.location
        self.recommandPlaces["accomodation"] = hotel
        self.recommandPlaces["places"]["attraction"] = self.attractionData #사용자 관심사에 맞는 장소 목록 2*(days-1)개 이상, 10개 이하. day = 1이라면, >= 1
        self.recommandPlaces["places"]["restaurant"] = [] #사용자 관심사에 맞는 식당 목록 2*(days-1)개 이상, 10개 이하. day = 1이라면 >= 1개
        
        #관심사에 따라 place search

        return self.recommandPlaces
          
    
    async def getPlaces(self, requestBody: TravelRequest) -> TravelResponse:
        self.attractionDData = await self.getAttractions( requestBody.attractions) #관광지 id기반으로 정보 받아옴
        midLocation = self.getLocation(self.attractionDData) #관광지들의 중심 좌표 받아옴
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