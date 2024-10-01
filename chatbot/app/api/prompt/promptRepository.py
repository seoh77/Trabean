from langchain_openai import OpenAIEmbeddings
from langchain_pinecone import PineconeVectorStore
import os
from dotenv import load_dotenv

# .env 파일에서 환경 변수를 로드
load_dotenv()

pinecone_api_key = os.environ.get("PINECONE_API_KEY")

class PineconeRepository:
    def __init__(self, index_name="chatbot-index"):
        self.index_name = index_name
        self.embedding = OpenAIEmbeddings(model='text-embedding-3-large')
        self.database = PineconeVectorStore.from_existing_index(index_name=index_name, embedding=self.embedding)

    def get_retriever(self, question, k=4):
        """
        Pinecone VectorStore에서 관련 문서를 검색하여 반환합니다.
        """
        retriever = self.database.as_retriever(search_kwargs={'k': k})
        relevant_docs = retriever.invoke(question)
        return relevant_docs
