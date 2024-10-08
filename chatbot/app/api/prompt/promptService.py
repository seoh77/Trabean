from langchain_core.output_parsers import StrOutputParser
from langchain_core.prompts import ChatPromptTemplate
from langchain_openai import OpenAIEmbeddings
from langchain_pinecone import PineconeVectorStore
from langchain_community.document_loaders import Docx2txtLoader
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain.chains import RetrievalQA
from langchain_openai import ChatOpenAI
from .promptSchemas import AnswerResponse
from .promptRepository import PineconeRepository
from langchain import hub
from pinecone import Pinecone

from dotenv import load_dotenv
import getpass
import os

# .env 파일에서 환경 변수를 로드
load_dotenv()

# PINECONE API 키 설정
if not os.getenv("PINECONE_API_KEY"):
    os.environ["PINECONE_API_KEY"] = getpass.getpass("Enter your Pinecone API key: ")
pinecone_api_key = os.environ.get("PINECONE_API_KEY")

# LangSmith API 키 설정
if not os.getenv("LANGSMITH_API_KEY"):
    os.environ["LANGSMITH_API_KEY"] = getpass.getpass("Enter your LangSmith API key: ")
langsmith_api_key = os.environ.get("LANGSMITH_API_KEY")

class QuestionService:
  def __init__(self, LLMmodel='gpt-4o', index_name="chatbot-index", emmodel='text-embedding-3-large', ragPrompt="rlm/rag-prompt", k=4):
    self.repository = PineconeRepository()
    self.llm = ChatOpenAI(model=LLMmodel)
    self.prompt = hub.pull(ragPrompt)  # 알맞은 프롬프트 생성 모델 불러오기
  
  def getQuestion(self, query):
    dictionary = self.repository.getDictionary()
    prompt = ChatPromptTemplate.from_template(f"""
      사용자의 질문을 보고, 우리의 사전을 참고해서 사용자의 질문을 변경해주세요.
      만약 변경할 필요가 없다고 판단된다면, 사용자의 질문을 변경하지 않아도 됩니다.
      그런 경우에는 질문만 리턴해주세요.
      사전: {dictionary}

      질문: {{question}}
  """)
    dictionary_chain = prompt | self.llm | StrOutputParser()
    question = dictionary_chain.invoke({"question": query, "dictionary": dictionary})
    return question
  
  def getAIMessage(self, query):
    question = self.getQuestion(query)
    retriever = self.repository.getRetriever(question)
    qa_chain = RetrievalQA.from_chain_type(  # RetrievalQA 객체 생성
        self.llm,  # llm 모델
        retriever=retriever,
        chain_type_kwargs={"prompt": self.prompt}
    )
    answer_dict = qa_chain.invoke({"query": question})
    if isinstance(answer_dict, dict) and 'result' in answer_dict:
        return AnswerResponse(answer=answer_dict['result'])

    # 만약 answer_dict가 예상치 못한 형식이면 오류를 반환
    raise ValueError(f"Unexpected response format: {answer_dict}")

# service = QuestionService()
# print(service.getAIMessage("환전 기능에 대해 알고 싶어"))