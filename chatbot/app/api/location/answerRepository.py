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
with open(file_path, 'r', encoding='utf-8') as file:
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

class PlaceFetcher:
    def __init__(self):
        pass

    #도시에 대한 위/경도 반환하는 함수
    def getCityLocation(self, country, city): 
             if country in locationData:
                # 도시가 존재하는지 확인
                if city in locationData[country]:
                    return {
                        "lat": locationData[country][city]["lat"],
                        "lon": locationData[country][city]["lon"]
                    }
                return None
             return None

    # 위/경도를 기준으로 좋은 호텔 찾기
    def getHotelLocation(self, lat, lon): 
        url = 'https://places.googleapis.com/v1/places:searchNearby'
        headers = {
            'Content-Type': 'application/json',  # JSON 형식으로 데이터 전송
            'X-Goog-Api-Key': GOOGLE_API_KEY,    # API KEY
            'X-Goog-FieldMask': 'places.id,places.displayName,places.googleMapsUri,places.location,places.rating,places.userRatingCount,places.primaryType,places.goodForChildren,places.paymentOptions,places.photos.name'
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
                        "center": {
                            "latitude": lat,
                            "longitude": lon
                        },
                        "radius": radius
                    }
                }
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

            radius += step #못찾으면 반경 늘려서 다시 찾기
        
        return 1
    
    # hotel데이터를 받아 가장 적합한 호텔 위치 반환
    def getCenterLocation(self, hotels):
        pass


    def fetchPlaces(self, request : TravelRequest) -> TravelResponse:
        route = {}
        for day in range(1, 3):  # 예시로 2일 일정
            route[day] = []
            for interest in interests:
                places = self._get_places(location, interest)
                for place in places:
                    route[day].append(place)
        return TravelResponse(location=location, route=route)


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
                    longitude=result["geometry"]["location"]["lng"]
                )
                places.append(place)

        return places

chat = PlaceFetcher()
# chat.getHotelLocation(32, 126.97)

