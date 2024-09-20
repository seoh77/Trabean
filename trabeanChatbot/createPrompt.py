# pip install --upgrade --quiet docx2txt langchain-community python-dotenv langchain langchain-openai langchain-text-splitters langchain-chroma flask chromadb openai

from langchain_community.document_loaders import Docx2txtLoader
from langchain_text_splitters import RecursiveCharacterTextSplitter

text_splitter = RecursiveCharacterTextSplitter(
    chunk_size=400,
    chunk_overlap=100,
)

loader = Docx2txtLoader('./trabean.docx')
document_list = loader.load_and_split(text_splitter=text_splitter)



from dotenv import load_dotenv
from langchain_openai import OpenAIEmbeddings

# 환경변수를 불러옴
load_dotenv()

# OpenAI에서 제공하는 Embedding Model을 활용해서 `chunk`를 vector화
embedding = OpenAIEmbeddings(model='text-embedding-3-large')

from langchain_chroma import Chroma

# 데이터를 처음 저장할 때 
#database = Chroma.from_documents(documents=document_list, embedding=embedding, collection_name='chroma-test', persist_directory="./chromaTest")

# 이미 저장된 데이터를 사용할 때 
database = Chroma(collection_name='chroma-test', persist_directory="./chromaTest", embedding_function=embedding)

query = '환전 기능에 대해 알고 싶어'

# `k` 값을 조절해서 얼마나 많은 데이터를 불러올지 결정
retrieved_docs = database.similarity_search(query, k=2)

from langchain_openai import ChatOpenAI

llm = ChatOpenAI(model='gpt-4o') #모델 불러오기

from langchain import hub

prompt = hub.pull("rlm/rag-prompt") #알맞은 프롬프트 생성 모델 불러오기

from langchain.chains import RetrievalQA
from langchain_chroma import Chroma

# Chroma.from_documents(documents=document_list, embedding=embedding, collection_name='chroma-test', persist_directory="./chromaTest")

# 이전에 저장한 database 가져오기
database = Chroma(collection_name='chroma-test', persist_directory="./chromaTest", embedding_function=embedding)

qa_chain = RetrievalQA.from_chain_type( # RetrievalQA 객체 생성
    llm, #llm 모델 
    retriever=database.as_retriever(),
    #as_retriever는 chroma 말고 다른 vector db에 사용가능함!
    chain_type_kwargs={"prompt": prompt}
)

ai_message = qa_chain.invoke({"query": query})
print(ai_message)