# main.py : 프로젝트의 전체적인 환경을 설정하는 파일
from app.api.location.answerController import locationAnswerRouter
from app.api.location.questionController import locationQuestionRouter # 라우터를 불러옵니다.
from fastapi import FastAPI, Depends
from starlette.middleware.cors import CORSMiddleware
from sqlalchemy.orm import Session
from app.db.database import SessionLocal
from app.db.models import Chatbot
from app.api.chatbot import router

app = FastAPI()

# CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 기존 코드에서 default_response_class 인자를 제거하고 단순 include_router로 수정
app.include_router(router, prefix="/api/chatbot")
app.include_router(locationAnswerRouter, prefix="/api/chatbot")
app.include_router(locationQuestionRouter, prefix="/api/chatbot")
