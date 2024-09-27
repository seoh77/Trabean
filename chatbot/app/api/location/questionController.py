from fastapi import APIRouter, HTTPException, Query
from pydantic import BaseModel
from typing import List, Union, Dict, Optional
from .models import ChatBotQuestion

# APIRouter 객체를 location_router로 정의
location_question_router = APIRouter()

# 데이터 모델 정의
class QuestionOption(BaseModel):
    question: str
    options: Union[List[str], str, Dict[str, List[str]], Dict[str, int]]  # dict 타입을 허용

class ChatBotResponse(BaseModel):
    questions: List[QuestionOption]

# ChatBot 객체 생성
chat_bot = ChatBotQuestion()

# GET 요청을 처리하는 엔드포인트
@location_question_router.get("/location/{question_index}")
async def get_question(question_index: int, country: Optional[str] = Query(None, description="국가명을 입력하세요.")):
    question = chat_bot.get_question(question_index, country)
    if "error" in question:
        raise HTTPException(status_code=400, detail=question["error"])
    return question