# 국가, 도시, 스타일, 예산, 우선순위 등을 정의하는 파일
# 환경 변수 로드
from typing import List, Dict
from dotenv import load_dotenv
import os
import json
import httpx 

# json 읽어오기
current_directory = os.path.dirname(os.path.abspath(__file__))  # 현재 파일 위치
project_root = os.path.abspath(os.path.join(current_directory, "../../../"))  # 최상위 디렉토리로 이동

# locatio Question 반환 class
class QuestionOption:
    def __init__(self):
        self.placeFetcher = PlaceFetcher()
    
    def readJson(self, fileName):
        file_path = os.path.join(project_root, "data", f"{fileName}.json")
        with open(file_path, "r", encoding="utf-8") as file:
            return json.load(file)

    # 국가 및 도시 목록 반환
    def getCountryCityMap(self):
        countryCityMap = self.readJson("countryCityMap")
        return countryCityMap

    # 여행 기간 옵션 반환
    def getTravelDurationOptions(self):
        travelDurationOptions = 5
        return travelDurationOptions

    # 이동 수단 옵션 반환
    def getTransportationsOptions(self):
        transportationsOptions = ["도보", "자전거", "자동차", "대중교통", "휠체어", "기타"]
        return transportationsOptions

    # 여행 테마 옵션 반환
    def getTravelStyleOptions(self):
        travelStyleOptions = [
            "체험/액티비티",
            "관람",
            "쇼핑",
            "먹방",
            "유명 관광지",
            "자연 속 힐링",
            "기타",
        ]
        return travelStyleOptions

    # 여행 우선순위 옵션 반환
    def getPriorityOptions(self):
        priorityOptions = ["높은 평점", "많은 리뷰", "짧은 이동거리", "여행 테마"]
        return priorityOptions
    
    
    # 여행 반경 반환
    def getRadius(self, trans: str):
        radius = self.readJson("transRadiusList")[trans]
        return radius

    
    # 도시에 대한 위도와 경도를 반환하는 함수
    # Args : country(str) : 국가 , city(str) : 도시
    # return : {"lat" : float, "lon" : float}
    def getCityLocation(self, country: str, city: str) -> dict:
        locationData = self.readJson("locationList")
    
        if country in locationData:
            # 도시가 존재하는지 확인
            if city in locationData[country]:
                return locationData[country][city]
        return None  # 국가나 도시가 없으면 None 반환
    
    
    # 위,경도를 중심으로 radius 반경 안에 travelStyle에 맞는 관광지 목록을 google API를 통해 검색
    # args : lat - 위도 , lon - 경도, raduis - 반경, travelStyle - 여행 스타일
    # reutnrn  : 관광지 id, name 목록
    async def getAttractionOptions(self, lat, lon, radius, travelStyle) -> List[Dict[str, str]]:
        attractionOptions = await self.placeFetcher.getAttraction(lat, lon, radius, travelStyle)
        if attractionOptions:
            newAttractionOptions = [{"id": option["id"], "name": option["displayName"]["text"]} for option in attractionOptions]
            return newAttractionOptions
        return []


load_dotenv()
GOOGLE_API_KEY = os.getenv("GOOGLE_API_KEY")

# 도시 근처 관광 명소 목록을 찾아와 주는 class
class PlaceFetcher: 
    def __init__(self):
        pass
        
    def readJson(self, fileName):
        file_path = os.path.join(project_root, "data", f"{fileName}.json")
        with open(file_path, "r", encoding="utf-8") as file:
            return json.load(file)
    

    # country, city에 대한 주요 K개의 관광지 반환
    # Args : country(str) : 국가 , city(str) : 도시
    # return : [ {"id": str, "name" : str}]
    async def getAttraction(self, lat, lon, radius, travelStyle) -> List[Dict[str, str]]:

        url = "https://places.googleapis.com/v1/places:searchNearby"
        headers = {
            "Content-Type": "application/json",  # JSON 형식으로 데이터 전송
            "X-Goog-Api-Key": GOOGLE_API_KEY,  # API KEY
            "X-Goog-FieldMask": "places.id,places.displayName.text",
        }

        attractionData = [] #최종 반환값 : 관심도에 알맞은 장소 목록
        num = len(travelStyle)
        K = 8 - num
        if K <= 1 : K = 2

        categories = self.readJson("categories")

        for style in travelStyle:
            travelStyles = categories[style]

            # 요청 본문 (바디)
            data = {
                "includedTypes": travelStyles,
                "languageCode": "ko",
                "maxResultCount": K,
                "locationRestriction": {
                    "circle": {
                        "center": {
                            "latitude": lat,
                            "longitude": lon,
                        },
                        "radius": radius,
                    }
                },
            }

            async with httpx.AsyncClient() as client:
                response = await client.post(url, headers=headers, json=data)

            if response.status_code == 200:
                responseData = response.json()
                if responseData:  # 제대로 된 관광지를 찾음
                    attractionData += responseData["places"]
                else:
                    print(f"No results found for radius {radius}. Increasing radius.")

            else:
                print("Error:", response.status_code, response.text)
                return
            
        return attractionData