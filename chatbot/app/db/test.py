# # 의존성으로 DB 세션을 가져오는 함수
# def get_db():
#     db = SessionLocal()
#     try:
#         yield db
#     finally:
#         db.close()


# # /hello 엔드포인트에서 chatbot 데이터 조회
# @app.get("/hello")
# def hello(db: Session = Depends(get_db)):
#     # chatbot 테이블에서 모든 데이터 조회
#     chatbot_data = db.query(Chatbot).all()
    
#     # 데이터를 출력 (콘솔에 출력)
#     for data in chatbot_data:
#         print(f"Location: {data.location}, Budget: {data.budget}")
    
#     return {"message": "데이터가 출력되었습니다."}
