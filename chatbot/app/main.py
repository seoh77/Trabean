# main.py : 프로젝트의 전체적인 환경을 설정하는 파일
from app.api.location.answerController import location_answer_router
from app.api.location.questionController import location_question_router # 라우터를 불러옵니다.
from fastapi import FastAPI, Depends
from starlette.middleware.cors import CORSMiddleware
from sqlalchemy.orm import Session
from app.db.database import SessionLocal
from app.db.models import Chatbot
from app.api.chatbot import router

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(router, prefix="/api/chatbot")
app.include_router(location_answer_router, prefix="/api/chatbot")
app.include_router(location_question_router, prefix="/api/chatbot")