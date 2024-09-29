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
            "midLatitude" : averageLatitude, 
            "midLongitude" : averageLongitude
        }
        return average

    # 관광지의 위/경도를 기준으로 좋은 호텔 찾기
    async def getHotelLocation(self, lat, lon):
        url = "https://places.googleapis.com/v1/places:searchNearby"
        headers = {
            "Content-Type": "application/json",  # JSON 형식으로 데이터 전송
            "X-Goog-Api-Key": GOOGLE_API_KEY,  # API KEY
            "X-Goog-FieldMask": "places.id,places.displayName,places.googleMapsUri,places.location,places.rating,places.userRatingCount,places.primaryType,places.goodForChildren,places.paymentOptions,places.photos.name",
        }
        # 요청 본문 (바디)
        radius = 10000.0  # 시작 반경
        max_radius = 50000.0  # 최대 반경
        step = 5000.0  # 반경 증가량

        while radius <= max_radius:
            # 요청 본문 (바디)
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
                response_data = response.json()
                if response_data:  # 제대로 된 호텔을 찾음
                    print("Success:", response_data)
                    return response_data
                else:
                    print(f"No results found for radius {radius}. Increasing radius.")

            else:
                print("Error:", response.status_code, response.text)
                return

            radius += step  # 못찾으면 반경 늘려서 다시 찾기

        return 1

    # hotel데이터를 받아 가장 적합한 호텔 위치 반환
    def getCenterLocation(self, hotels):
        pass

    # Google Places API를 이용하여 사용자 관심사에 맞는 장소 리스트를 가져오는 함수.
    def fetchRecommendedPlaces(self) -> List[Place]:

        country = self.location.get("country")
        city = self.location.get("city")
        interest = self.preferences.get("interest", [])

        places = []
        for category in interest:
            query = f"{category} in {city}, {country}"
            url = f"https://maps.googleapis.com/maps/api/place/textsearch/json?query={query}&key={GOOGLE_API_KEY}"

            response = requests.get(url)
            results = response.json().get("results", [])

            for result in results:
                place = Place(
                    name=result["name"],
                    address=result["formatted_address"],
                    rating=result.get("rating", 0),
                    category=category,
                    latitude=result["geometry"]["location"]["lat"],
                    longitude=result["geometry"]["location"]["lng"],
                )
                places.append(place)

        return places
    
    async def getPlaces(self, request: TravelRequest) -> TravelResponse:
        attractions = await self.getAttractions(request["attractions"]) #관광지 정보 받아옴
        midLocation = self.getLocation(attractions) #관광지들의 중심 좌표 받아옴
        hotel = self.getHotelLocation(midLocation["lat"], midLocation["lon"]) # 호텔 정보 받아옴
        restaurants = {}




