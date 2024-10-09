from fastapi import FastAPI
from app.api.location.answerController import locationAnswerRouter
from app.api.location.questionController import locationQuestionRouter
from app.api.prompt.promptController import promptRouter
from starlette.middleware.cors import CORSMiddleware
from app.api.chatbot import router
import httpx
from contextlib import asynccontextmanager
from py_eureka_client import eureka_client

# Eureka 서버 URL 및 서비스 정보 설정
EUREKA_SERVER_URL = "http://j11a604.p.ssafy.io:8761/eureka"
SERVICE_NAME = "chatbot"
INSTANCE_IP = "j11a604.p.ssafy.io"  # EC2의 공인 IP
INSTANCE_PORT = 8082


@asynccontextmanager
async def lifespan(app: FastAPI):
    # CORS 설정은 여기에 포함되지 않음
    await eureka_client.init_async(
        eureka_server=EUREKA_SERVER_URL,
        app_name=SERVICE_NAME,
        instance_port=INSTANCE_PORT,
        instance_host=INSTANCE_IP,
        instance_ip=INSTANCE_IP
    )
    print("eureka 등록!")
    yield
    # 애플리케이션 종료 시 Eureka에서 해제
    await eureka_client.stop_async()

# FastAPI 인스턴스를 생성할 때 lifespan을 함께 설정
app = FastAPI(lifespan=lifespan)

# CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 라우터 등록
app.include_router(router, prefix="/api/chatbot")
app.include_router(locationAnswerRouter, prefix="/api/chatbot")
app.include_router(locationQuestionRouter, prefix="/api/chatbot")
app.include_router(promptRouter, prefix="/api/chatbot")
print("chatbot 앱 시작!!!")
