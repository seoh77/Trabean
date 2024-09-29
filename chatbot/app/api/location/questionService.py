from typing import List, Union, Dict, Optional
from .questionRepository import QuestionOption  # 필요한 모듈 임포트
import asyncio  # 비동기 함수 사용을 위한 모듈

class Question:
    def __init__(self, questionText: str, options: Union[List[str], List[Dict[str, str]], str, Dict[str, List[str]]] = None):
        self.questionText = questionText
        self.options = options if options else []  # 기본 옵션 값 설정

    def to_dict(self):
        return {
            "question": self.questionText,
            "options": self.options
        }

class ChatBotQuestion:
    def __init__(self):
        self.questionOption = QuestionOption()
        self.countryCityMap = self.questionOption.getCountryCityMap()
        self.countryMap = list(self.countryCityMap.keys())
        self.travelDurationOptions = self.questionOption.getTravelDurationOptions()
        self.transportationsOptions = self.questionOption.getTransportationsOptions()
        self.travelStyleOptions = self.questionOption.getTravelStyleOptions()
        self.priorityOptions = self.questionOption.getPriorityOptions()
        self.questions = [
            Question("어느 국가를 여행하시나요?", self.countryMap),  # 국가 목록을 미리 설정
            Question("어느 도시를 여행하시나요?", []),  # 국가 선택에 맞는 도시 목록 설정
            Question("여행 기간은 총 몇 일인가요?", self.travelDurationOptions),
            Question("주로 어떤 이동 수단을 이용하시나요?", self.transportationsOptions),
            Question("선호하는 여행 테마는 무엇인가요? (중복 선택 가능)", self.travelStyleOptions),
            Question("방문하고 싶은 관광명소를 선택해주세요.", []),
            Question("여행 시 중요하게 생각하는 우선순위를 차례로 선택해주세요.", self.priorityOptions)
        ]

    # 비동기 함수로 변경하여 country에 대한 주요 city 반환
    async def initializeQuestions(self, country: Optional[str] = None) -> List[Question]:
        await asyncio.sleep(0.1)
        city_options = self.countryCityMap.get(country, []) if country else []
        return city_options

    # 비동기적으로 getQuestion 메서드를 정의
    async def getQuestion(self, question_index: int, country: str, city: str, days: int, trans: str) -> dict:
        # 유효한 질문 인덱스인지 확인
        if question_index < 0 or question_index >= len(self.questions):
            return {"error": "Invalid question number"}
        
        question = self.questions

        if question_index == 1:
            if not country:
                return "error : Country parameter is missing or empty"
            # 비동기적으로 초기화된 질문 목록 설정
            question[1].options = await self.initializeQuestions(country)

        elif question_index == 5:
            if days is None:
                return {"error": "Days parameter is required for question index 3"}
            
            # days를 이용해 질문을 설정 (예: 최대 선택 가능 개수)
            question[5].questionText = f"방문하고 싶은 관광명소를 선택해주세요. (최대 {days}개)"
            
            # 비동기적으로 관광명소 옵션을 가져옴
            question[5].options = await self.questionOption.getAttractionOptions(country, city, trans)

        return question[question_index].to_dict()
