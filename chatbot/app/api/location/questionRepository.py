# 국가, 도시, 스타일, 예산, 우선순위 등을 정의하는 파일
# 환경 변수 로드
from typing import List, Dict
from dotenv import load_dotenv
import os
import json
import httpx 
import random

# locatio Question 반환 class
class QuestionOption:
    def __init__(self):
        self.placeFetcher = PlaceFetcher()

    # 국가 및 도시 목록 반환
    def getCountryCityMap(self):
        return countryCityMap

    # 여행 기간 옵션 반환
    def getTravelDurationOptions(self):
        return travelDurationOptions

    # 이동 수단 옵션 반환
    def getTransportationsOptions(self):
        return transportationsOptions

    # 여행 테마 옵션 반환
    def getTravelStyleOptions(self):
        return travelStyleOptions

    # 여행 우선순위 옵션 반환
    def getPriorityOptions(self):
        return priorityOptions
    
    # 여행 관광지 반환
    async def getAttractionOptions(self, requestBody) -> List[Dict[str, str]]:
        attractionOptions = await self.placeFetcher.getAttraction(requestBody)

        if attractionOptions:
            newAttractionOptions = [{"id": option["id"], "name": option["displayName"]["text"]} for option in attractionOptions]
            return newAttractionOptions
        return []


# 국가별 위/경도 목록
load_dotenv()
base_dir = os.path.dirname(os.path.abspath(__file__))
file_path = os.path.join(base_dir, "locationList.json")
with open(file_path, "r", encoding="utf-8") as file:
    locationData = json.load(file)

load_dotenv()
GOOGLE_API_KEY = os.getenv("GOOGLE_API_KEY")

# 도시 근처 관광 명소 목록을 찾아와 주는 class
class PlaceFetcher: 
    def __init__(self):
        self.locationData = locationData  # locationData를 클래스 속성으로 저장

    # 도시에 대한 위도와 경도를 반환하는 함수
    # Args : country(str) : 국가 , city(str) : 도시
    # return : {"lat" : float, "lon" : float}
    def getCityLocation(self, country: str, city: str) -> dict:
        # 국가가 locationData에 있는지 확인
        if country in self.locationData:
            # 도시가 존재하는지 확인
            if city in self.locationData[country]:
                return {
                    "lat": self.locationData[country][city]["lat"],
                    "lon": self.locationData[country][city]["lon"],
                }
        return None  # 국가나 도시가 없으면 None 반환


    # city에 대한 주요 K개의 관광지 반환
    # Args : country(str) : 국가 , city(str) : 도시
    # return : [ {"id": str, "name" : str}]
    async def getAttraction(self, requestBody) -> List[Dict[str, str]]:
        country = requestBody.country
        city = requestBody.city
        trans = requestBody.trans
        location = self.getCityLocation(country, city)
        radius = 0
        
        if trans == "기타" :
            radius = random.randint(1000, 30000)
        else :
            radius = radiusList[trans]

        url = "https://places.googleapis.com/v1/places:searchNearby"
        headers = {
            "Content-Type": "application/json",  # JSON 형식으로 데이터 전송
            "X-Goog-Api-Key": GOOGLE_API_KEY,  # API KEY
            "X-Goog-FieldMask": "places.id,places.displayName.text",
        }

        attractionData = [] #최종 반환값 : 관심도에 알맞은 장소 목록
        num = len(requestBody.travelStyle)
        K = 8 - num
        if K <= 1 : K = 2

        for style in requestBody.travelStyle:
            travelStyles = categories[style]

            # 요청 본문 (바디)
            data = {
                "includedTypes": travelStyles,
                "languageCode": "ko",
                "maxResultCount": K,
                "locationRestriction": {
                    "circle": {
                        "center": {
                            "latitude": location["lat"],
                            "longitude": location["lon"],
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
    

# 이동 수단에 따른 반경 목록
# 이동 수단에 따른 반경 목록
radiusList = {
  "도보": 1000,
  "자전거": 3000,
  "자동차": 10000,
  "대중교통": 20000,
  "휠체어": 800
}

# 국가 및 도시에 대한 답변 목록
countryCityMap = {
    "한국": [
        "서울",
        "부산",
        "인천",
        "제주",
        "경주",
        "대구",
        "광주",
        "속초",
        "대전",
        "수원",
    ],
    "미국": [
        "뉴욕",
        "로스앤젤레스",
        "샌프란시스코",
        "라스베이거스",
        "시카고",
        "워싱턴 D.C.",
        "마이애미",
        "보스턴",
        "올랜도",
        "뉴올리언스",
    ],
    "독일": [
        "베를린",
        "뮌헨",
        "프랑크푸르트",
        "쾰른",
        "함부르크",
        "하이델베르크",
        "슈투트가르트",
        "드레스덴",
        "뒤셀도르프",
        "라이프치히",
    ],
    "프랑스": [
        "파리",
        "니스",
        "마르세유",
        "리옹",
        "보르도",
        "툴루즈",
        "낭트",
        "릴",
        "엑상프로방스",
        "칸",
    ],
    "일본": [
        "도쿄",
        "오사카",
        "교토",
        "삿포로",
        "후쿠오카",
        "나고야",
        "히로시마",
        "오키나와",
        "가마쿠라",
        "나라",
    ],
    "중국": [
        "베이징",
        "상하이",
        "광저우",
        "시안",
        "청두",
        "선전",
        "항저우",
        "쿤밍",
        "난징",
        "하얼빈",
    ],
    "영국": [
        "런던",
        "에든버러",
        "맨체스터",
        "리버풀",
        "글래스고",
        "브리스틀",
        "옥스퍼드",
        "케임브리지",
        "카디프",
        "요크",
    ],
    "스위스": [
        "취리히",
        "제네바",
        "루체른",
        "인터라켄",
        "바젤",
        "베른",
        "로잔",
        "체르마트",
        "루가노",
        "그린델발트",
    ],
    "캐나다": [
        "토론토",
        "밴쿠버",
        "몬트리올",
        "퀘벡시티",
        "오타와",
        "캘거리",
        "에드먼턴",
        "빅토리아",
        "위니펙",
        "해밀턴",
    ],
}

# 여행 기간에 대한 답변 목록
travelDurationOptions = 5

# 이동 수단에 대한 답변 목록
transportationsOptions = ["도보", "자전거", "자동차", "대중교통", "휠체어", "기타"]

# 여행 테마에 대한 답변 목록
travelStyleOptions = [
    "체험/액티비티",
    "관람",
    "쇼핑",
    "먹방",
    "유명 관광지",
    "자연 속 힐링",
    "기타",
]

# 여행 우선순위에 대한 답변 목록
priorityOptions = ["높은 평점", "많은 리뷰", "짧은 이동거리", "여행 테마"]


# 관광지에 대한 답변 목록
categories = {
    "체험/액티비티": [
        "amusement_park", "aquarium", "bowling_alley", "zoo", "museum"
    ],
    "관람": [
        "art_gallery", "church", "hindu_temple", "synagogue", "performing_arts_theater"
    ],
    "쇼핑": [
        "book_store", "clothing_store", 
        "department_store", "electronics_store", "florist", "furniture_store", 
        "hardware_store", "home_goods_store", "jewelry_store", "liquor_store", 
        "shoe_store", "shopping_mall", "supermarket", "store"
    ],
    "먹방": [
        "bakery", "bar", "cafe", "restaurant"
    ],
    "유명 관광지": [
        "tourist_attraction", "historical_landmark"
    ],
    "자연 속 힐링": [
        "park", "campground", "rv_park"
    ],
    "기타": [
        "book_store", "athletic_field", "stadium", "library"
    ]
}