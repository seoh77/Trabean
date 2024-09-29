from typing import List
from .answerSchemas import Place
import itertools

class ChatBotAnswer:
    def __init__(self):
        pass

    # DP 알고리즘을 사용하여 주어진 일수(days)에 맞는 최적의 여행 경로를 생성.
    def getTravelRoutes(places: List[Place], days: int, transportation: str) -> List[List[Place]]:

        optimal_route = []
        chunk_size = len(places) // days  # 하루에 방문할 장소 수
        for i in range(days):
            daily_route = places[i * chunk_size : (i + 1) * chunk_size]
            optimal_route.append(daily_route)

        return optimal_route
