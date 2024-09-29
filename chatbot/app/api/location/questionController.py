from fastapi import APIRouter, HTTPException, Query
from fastapi.responses import JSONResponse
from typing import Optional
from .questionSchemas import QuestionOption
from .questionService import ChatBotQuestion

# APIRouter 객체를 정의
locationQuestionRouter = APIRouter(
    default_response_class=JSONResponse
)

# ChatBot 객체 생성
chatBot = ChatBotQuestion()

# GET 요청을 처리하는 엔드포인트
@locationQuestionRouter.get("/location/{questionIndex}", response_model=QuestionOption)
async def getQuestion(questionIndex: int, country: Optional[str] = Query(None, description="국가명을 입력하세요.")):
    question = chatBot.getQuestion(questionIndex, country)
    if "error" in question:
        raise HTTPException(status_code=400, detail=question["error"])
    return question