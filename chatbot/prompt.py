from langchain_core.output_parsers import StrOutputParser
from langchain_core.prompts import ChatPromptTemplate
from langchain_openai import OpenAIEmbeddings
from langchain_pinecone import PineconeVectorStore
from langchain_community.document_loaders import Docx2txtLoader
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain.chains import RetrievalQA
from langchain_openai import ChatOpenAI
from pinecone import Pinecone
from langchain import hub
import getpass
import os
from dotenv import load_dotenv

# .env 파일에서 환경 변수를 로드
load_dotenv()

# 텍스트 스플리터 설정
text_splitter = RecursiveCharacterTextSplitter(
    chunk_size=400,
    chunk_overlap=100,
)

loader = Docx2txtLoader('../trabean.docx')
document_list = loader.load_and_split(text_splitter=text_splitter)

# PINECONE API 키 설정
if not os.getenv("PINECONE_API_KEY"):
    os.environ["PINECONE_API_KEY"] = getpass.getpass("Enter your Pinecone API key: ")
pinecone_api_key = os.environ.get("PINECONE_API_KEY")

# LangSmith API 키 설정
if not os.getenv("LANGSMITH_API_KEY"):
    os.environ["LANGSMITH_API_KEY"] = getpass.getpass("Enter your LangSmith API key: ")
langsmith_api_key = os.environ.get("LANGSMITH_API_KEY")

index_name = "chatbot-index"
pc = Pinecone(api_key=pinecone_api_key)

embedding = OpenAIEmbeddings(model='text-embedding-3-large')
database = PineconeVectorStore.from_existing_index(index_name=index_name, embedding=embedding)
# database = PineconeVectorStore.from_documents(document_list, index_name=index_name, embedding=embedding)

query = 'Trabean 통장의 환전 기능에 대해 알고 싶어'

dictionary = ["사람을 나타내는 표현 -> 거주자", "Trabean -> bean"]

prompt = ChatPromptTemplate.from_template(f"""
    사용자의 질문을 보고, 우리의 사전을 참고해서 사용자의 질문을 변경해주세요.
    만약 변경할 필요가 없다고 판단된다면, 사용자의 질문을 변경하지 않아도 됩니다.
    그런 경우에는 질문만 리턴해주세요
    사전: {dictionary}

    질문: {{question}}
""")

llm = ChatOpenAI(model='gpt-4o')  # 모델 불러오기

#keyword 사전을 이용해 query 수정하기
dictionary_chain = prompt | llm | StrOutputParser()
question = dictionary_chain.invoke({"question": query, "dictionary" : dictionary})

print(question)

# `k` 값을 조절해서 얼마나 많은 데이터를 불러올지 결정
retriever = database.as_retriever(search_kwargs={'k': 4})
relevant_docs = retriever.invoke(question)

# 검색된 문서 출력
for doc in relevant_docs:
    print(doc.page_content)

prompt = hub.pull("rlm/rag-prompt")  # 알맞은 프롬프트 생성 모델 불러오기

qa_chain = RetrievalQA.from_chain_type(  # RetrievalQA 객체 생성
    llm,  # llm 모델
    retriever=retriever,
    chain_type_kwargs={"prompt": prompt}
)

answer = qa_chain.invoke({"query": question})

print(answer)