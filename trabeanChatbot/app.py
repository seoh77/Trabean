from flask import Flask, request, jsonify
from langchain_community.document_loaders import Docx2txtLoader
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_openai import OpenAIEmbeddings, ChatOpenAI
from langchain_chroma import Chroma
from langchain.chains import RetrievalQA
from dotenv import load_dotenv
from langchain import hub

# 환경변수 로드
load_dotenv()

app = Flask(__name__)

@app.route('/ask', methods=['POST'])
def ask():
    query = request.data.decode('utf-8')  # Spring에서 보내온 질문

    # OpenAI Embedding 및 ChromaDB 설정
    embedding = OpenAIEmbeddings(model='text-embedding-3-large')
    database = Chroma(collection_name='chroma-test', persist_directory="./chromaTest", embedding_function=embedding)

    # LLM 및 Prompt 설정
    llm = ChatOpenAI(model='gpt-4o')
    prompt = hub.pull("rlm/rag-prompt")

    # RetrievalQA 체인 생성 및 실행
    qa_chain = RetrievalQA.from_chain_type(llm, retriever=database.as_retriever(), chain_type_kwargs={"prompt": prompt})

    # AI 모델에 질문을 보내고 응답을 받음
    ai_message = qa_chain.invoke({"query": query})

    # 응답을 JSON 형태로 반환 (ensure_ascii=False 옵션 추가)
    return jsonify({"answer": ai_message}, ensure_ascii=False)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
