from fastapi import FastAPI
from app.api.location.answerController import locationAnswerRouter
from app.api.location.questionController import locationQuestionRouter
from app.api.prompt.promptController import promptRouter
from starlette.middleware.cors import CORSMiddleware
from app.api.chatbot import router
import httpx

app = FastAPI()

# CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Eureka 서버 URL 및 서비스 정보 설정
EUREKA_SERVER_URL = "http://j11a604.p.ssafy.io:8761/eureka/"
SERVICE_NAME = "chatbot"
INSTANCE_IP = "0.0.0.0"
INSTANCE_PORT = 8082

# 애플리케이션을 Eureka에 등록하는 함수
async def register_to_eureka():
    async with httpx.AsyncClient() as client:
        payload = {
            "instance": {
                "instanceId": f"{SERVICE_NAME}_{INSTANCE_IP}_{INSTANCE_PORT}",
                "app": SERVICE_NAME,
                "hostName": INSTANCE_IP,
                "ipAddr": INSTANCE_IP,
                "port": {
                    "$": INSTANCE_PORT,
                    "@enabled": "true"
                },
                "status": "UP",
                "dataCenterInfo": {
                    "name": "MyOwn"
                },
                "leaseInfo": {
                    "durationInSecs": 30,
                    "registrationTimestamp": 0,
                    "lastRenewalTimestamp": 0,
                    "evictionTimestamp": 0,
                    "serviceUpTimestamp": 0,
                }
            }
        }
        try:
            response = await client.post(f"{EUREKA_SERVER_URL}/apps/{SERVICE_NAME}", json=payload)
            print(f"Eureka 등록 응답 코드: {response.status_code}")
            print(f"Eureka 등록 응답 내용: {response.text}")
            if response.status_code == 204:
                print("Eureka에 성공적으로 등록되었습니다!")
            else:
                print("Eureka 등록 실패")
        except Exception as e:
            print(f"Eureka 등록 중 오류 발생: {e}")


# lifespan 이벤트 핸들러 사용
@app.on_event("startup")
async def startup_event():
  print("Eureka 연결 시도...")
  await register_to_eureka()  # Eureka에 등록

@app.on_event("shutdown")
async def shutdown_event():
  print("Eureka 해제!")
  # 필요한 경우 deregister 로직을 추가할 수 있습니다
  pass

# 라우터 등록
app.include_router(router, prefix="/api/chatbot")
app.include_router(locationAnswerRouter, prefix="/api/chatbot")
app.include_router(locationQuestionRouter, prefix="/api/chatbot")
app.include_router(promptRouter, prefix="/api/chatbot")
