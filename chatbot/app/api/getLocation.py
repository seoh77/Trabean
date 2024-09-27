from pathlib import Path
import json

# json 파일을 읽어오기
def load_country_data(file_path):
    with open(file_path, "r", encoding="utf-8") as f:
        return json.load(f)


# 국가와 도시를 입력받아 위도와 경도를 반환
def get_location(country_name, city_name, data):
    try:
        lat = data[country_name][city_name]["lat"]
        lon = data[country_name][city_name]["lon"]
        return {"lat": lat, "lon": lon}
    except KeyError:
        return f"{country_name}의 {city_name}에 대한 정보가 없습니다."


def load_file(filename):
    # 파일 데이터 로드
    current_dir = Path(__file__).resolve().parent
    file_path = current_dir.parent.parent / 'data' / filename
    country_data = load_country_data(file_path)
    return country_data

# 함수 사용 예시
country_data = load_file('placeList.json')
result = get_location("한국", "서울", country_data)
print(result)  # {"lat": 37.5665, "lon": 126.9780}
