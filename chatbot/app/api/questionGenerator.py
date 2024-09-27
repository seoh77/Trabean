from fastapi import FastAPI
from pydantic import BaseModel
from typing import List, Dict, Union

app = FastAPI()

# 질문과 답변 데이터를 다룰 Question 클래스 정의
class Question:
    def __init__(self, question_text: str, options: Union[List[str], str]):
        self.question_text = question_text
        self.options = options

    def to_dict(self):
        return {
            "question": self.question_text,
            "options": self.options
        }

# ChatBot 클래스에서 질문들을 관리
class ChatBot:
    def __init__(self):
        # 국가 및 도시에 대한 답변 목록 설정
        self.country_city_map = {
            "한국": ["서울", "부산", "제주", "인천"],
            "미국": ["뉴욕", "로스앤젤레스", "샌프란시스코"],
            "독일": ["베를린", "뮌헨", "프랑크푸르트"],
            "프랑스": ["파리", "리옹", "니스"],
            "일본": ["도쿄", "오사카", "교토"],
            "중국": ["베이징", "상하이", "홍콩"],
            "영국": ["런던", "맨체스터", "에든버러"],
            "스위스": ["취리히", "제네바", "베른"],
            "캐나다": ["토론토", "밴쿠버", "몬트리올"]
        }

        # 질문과 옵션 설정
        self.questions = [
            Question("어느 국가를 여행하시나요?", list(self.country_city_map.keys()) + ["기타"]),
            Question("어느 도시를 여행하시나요?", "해당 국가에 맞는 리스트"),
            Question("여행 기간은 총 몇일인가요?", ["당일치기", "2일~7일"]),
            Question("선호하는 여행 스타일은?", ["체험/액티비티", "쇼핑", "먹방", "유명 관광지", "SNS 핫플레이스", "자연 속 힐링"]),
            Question("총 예산은 얼마인가요?", "text"),
            Question("여행시 중요하게 생각하는 우선순위를 선택해주세요.", ["평점", "이동거리", "가격", "테마"]),
            Question("누구와 함께 여행을 떠나나요?", ["혼자", "친구와", "가족과", "아이와", "연인과", "부모님과", "기타"])
        ]

    def get_questions(self) -> List[Dict[str, Union[str, List[str]]]]:
        # 질문 객체들을 dict 형식으로 변환하여 반환
        return [question.to_dict() for question in self.questions]

# 데이터 모델 정의
class QuestionOption(BaseModel):
    question: str
    options: Union[List[str], str]  # 답변이 리스트일 수도 있고, 텍스트 입력일 수도 있음

class ChatBotResponse(BaseModel):
    questions: List[QuestionOption]

# ChatBot 객체 생성
chat_bot = ChatBot()

# 엔드포인트 구현
@app.post("/api/chatbot/location", response_model=ChatBotResponse)
async def get_questions():
    # ChatBot 클래스에서 질문 목록을 가져와 응답으로 반환
    questions = chat_bot.get_questions()
    response = ChatBotResponse(
        questions=[QuestionOption(**question) for question in questions]
    )
    return response
