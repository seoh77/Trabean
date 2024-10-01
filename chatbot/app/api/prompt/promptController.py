from fastapi import APIRouter, HTTPException
from fastapi.responses import JSONResponse
from .promptService import QueryService
from .promptSchemas import AnswerResponse, QuestionRequest

# FastAPI 라우터 인스턴스 생성
promptRouter = APIRouter(default_response_class=JSONResponse)

# QueryService 객체 생성
query_service = QueryService()

@promptRouter.post("/question", response_model=AnswerResponse)
async def recommend_location(travel_request: QuestionRequest):
    try:
        ai_response = query_service.get_ai_message(travel_request.question)
        return AnswerResponse(question=travel_request.question, answer=ai_response)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))