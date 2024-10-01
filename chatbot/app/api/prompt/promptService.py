from .promptRepository import PineconeRepository
from langchain import hub
from langchain.chains import RetrievalQA
from .promptSchemas import QuestionRequest
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser
from langchain_openai import ChatOpenAI

class QueryService:
    def __init__(self, model='gpt-4o'):
        self.llm = ChatOpenAI(model=model)
        self.repository = PineconeRepository()

    def get_llm(self):
        """
        지정된 LLM 모델을 반환합니다.
        """
        return self.llm

    def get_question(self, query):
        """
        keyword 사전을 이용하여 사용자의 query를 수정합니다.
        """
        dictionary = ["사람을 나타내는 표현 -> 거주자", "Trabean -> bean"]
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

    def get_ai_message(self, query):
        """
        사용자의 질문에 대한 AI 답변을 반환합니다.
        """
        # 사전 처리된 질문을 가져옴
        question = self.get_question(query)

        # Pinecone에서 관련 문서를 검색하여 Retriever 생성
        retriever = self.repository.get_retriever(question, 4)

        # 적합한 프롬프트 생성
        prompt = hub.pull("rlm/rag-prompt")

        # RetrievalQA 객체 생성
        qa_chain = RetrievalQA.from_chain_type(
            llm=self.llm,
            retriever=retriever,
            chain_type_kwargs={"prompt": prompt}
        )

        # 질문에 대한 AI 답변 반환
        answer = qa_chain.invoke({"query": question})
        return answer