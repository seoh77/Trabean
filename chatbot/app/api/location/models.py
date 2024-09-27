from typing import List, Union, Dict, Optional
from .data import country_city_map, travel_duration_options, travel_style_options, priority_options, companions_options

class Question:
    def __init__(self, question_text: str, options: Union[List[str], str, Dict[str, List[str]]] = None):
        self.question_text = question_text
        self.options = options if options else []  # 기본 옵션 값 설정

    def to_dict(self):
        return {
            "question": self.question_text,
            "options": self.options
        }

class ChatBotQuestion:
    def __init__(self):
        self.questions = [
            Question("어느 국가를 여행하시나요?", list(country_city_map.keys())),  # 국가 목록을 미리 설정
            Question("어느 도시를 여행하시나요?", []),  # 국가 선택에 맞는 도시 목록 설정
            Question("여행 기간은 총 몇 일인가요?", travel_duration_options),
            Question("누구와 함께 여행을 떠나나요?", companions_options),
            Question("선호하는 여행 스타일은 무엇인가요?", travel_style_options),
            Question("총 예산은 얼마인가요?", "text"),
            Question("여행 시 중요하게 생각하는 우선순위를 선택해주세요.", priority_options)
        ]

    def initialize_questions(self, country: Optional[str] = None) -> List[Question]:
        # country 값이 country_city_map에 있는지 확인하고, 있으면 해당 값을 반환 
        city_options = country_city_map.get(country, []) if country else []
        return city_options

    def get_question(self, question_index: int, country: Optional[str] = None) -> dict:
        # 유효한 질문 인덱스인지 확인
        if question_index < 0 or question_index >= len(self.questions):
            return {"error": "Invalid question number"}
        
        question = self.questions

        if question_index == 1:
            if not country:
                return "error : Country parameter is missing or empty"
            question[1].options = self.initialize_questions(country)  #질문 목록을 새로 초기화
        return question[question_index].to_dict()

