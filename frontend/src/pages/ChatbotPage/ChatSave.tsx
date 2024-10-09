import React, { useState, useEffect, useRef } from "react";
import axios from "axios";
import TopBar from "../../components/TopBar";
import bean from "../../assets/bean_chat.png";

interface Option {
  id: string;
  description: string;
}

interface Message {
  key: string;
  type: "user" | "bot";
  content: string;
}

const Chatbot: React.FC = () => {
  const [options, setOptions] = useState<Option[]>([]);
  const [userInput, setUserInput] = useState(""); // 사용자 입력 관리
  const [messages, setMessages] = useState<Message[]>([]); // 채팅 메시지 목록 관리
  const [mode, setMode] = useState("question"); // 모드 상태 추가
  // const [locationOptions, setLocationOptions] = useState<Option[]>([]);
  // const [currencyOptions, setCurrencyOptions] = useState<Option[]>([]);
  // const [productOptions, setProductOptions] = useState<Option[]>([]);
  // const [consumption, consumption] = useState<Option[]>([]);
  // const [saving, setSaving] = useState<Option[]>([]);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const currencyList = [
    { id: "USD", description: "미국 (USD)" },
    { id: "EUR", description: "유럽 (EUR)" },
    { id: "JPY", description: "일본 (JPY)" },
    { id: "CNY", description: "중국 (CNY)" },
    { id: "GBP", description: "영국 (GBP)" },
    { id: "CHF", description: "스위스 (CHF)" },
    { id: "CAD", description: "캐나다 (CAD)" },
  ];

  // 가로 스크롤 고정
  useEffect(() => {
    document.body.style.overflowX = "hidden";
    return () => {
      document.body.style.overflowX = ""; // 컴포넌트 언마운트 시 원상복구
    };
  }, []);

  // API 요청을 통해 초기 메시지와 옵션 목록 가져오기
  useEffect(() => {
    const fetchChatbotData = async () => {
      try {
        const newResponse = await axios.get(
          "http://localhost:8082/api/chatbot/start",
        );

        // API 응답의 메시지와 옵션 데이터를 상태에 설정
        setOptions(newResponse.data.options);
        setMessages((prev) => [
          ...prev,
          {
            key: `bot-${Date.now()}`,
            type: "bot",
            content: newResponse.data.message,
          },
        ]); // 서버로부터 받은 초기 챗봇 메시지 추가
      } catch (error) {
        console.error("챗봇 데이터 가져오기 오류:", error);
      }
    };

    fetchChatbotData();
  }, []);

  // 옵션 클릭 시 해당 옵션의 id와 설명을 사용하여 처리
  const handleOptionClick = (id: string, description: string) => {
    let responseMessage = ""; // 챗봇 응답 메시지 변수
    setMode(id); // 모드 변경
    switch (id) {
      case "location":
        responseMessage = "여행 장소를 추천해 드릴게요!";
        break;
      case "exchange":
        setOptions(currencyList);
        responseMessage = "기준통화를 선택해주세요";
        break;
      case "product":
        responseMessage = "적합한 상품을 추천해 드리겠습니다.";
        break;
      case "consumption":
        responseMessage = "소비 패턴을 분석 중입니다...";
        break;
      case "saving":
        responseMessage = "저축 목표 관리 정보를 제공해 드릴게요.";
        break;
      default:
        responseMessage = "알 수 없는 요청입니다.";
    }

    // 사용자 메시지와 챗봇의 응답 메시지를 순차적으로 추가
    setMessages((prev) => [
      ...prev,
      {
        key: `user-${Date.now()}-${Math.random()}`,
        type: "user",
        content: description,
      }, // 사용자 메시지 추가
      {
        key: `bot-${Date.now()}-${Math.random()}`,
        type: "bot",
        content: responseMessage,
      }, // 챗봇 응답 메시지 추가
    ]);
  };

  // 환전 기능
  const handleCurrencyClick = (id: string, description: string) => {
    const responseMessage = "대상 통화를 선택해주세요";
    // 사용자 메시지와 챗봇의 응답 메시지를 순차적으로 추가
    setMessages((prev) => [
      ...prev,
      {
        key: `user-${Date.now()}-${Math.random()}`,
        type: "user",
        content: description,
      }, // 사용자 메시지 추가
      {
        key: `bot-${Date.now()}-${Math.random()}`,
        type: "bot",
        content: responseMessage,
      }, // 챗봇 응답 메시지 추가
    ]);
  };

  // 사용자 텍스트 입력 핸들러
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setUserInput(e.target.value);
  };

  // 전송 버튼 클릭 시 사용자 메시지 추가 및 챗봇 응답 처리
  const handleSendMessage = () => {
    if (userInput.trim()) {
      // 사용자 메시지 추가
      setMessages((prev) => [
        ...prev,
        {
          key: `user-${Date.now()}-${Math.random()}`,
          type: "user",
          content: userInput,
        },
      ]);

      // 챗봇의 응답 메시지 예시
      setMessages((prev) => [
        ...prev,
        {
          key: `bot-${Date.now()}-${Math.random()}`,
          type: "bot",
          content: "챗봇이 입력한 메시지를 처리 중입니다...",
        },
      ]);

      setUserInput(""); // 입력 필드 초기화
    }
  };

  // 메시지가 추가될 때마다 스크롤을 최신 메시지로 이동
  useEffect(() => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [messages]);

  return (
    <div className="overflow-hidden w-full min-h-screen">
      {/* 상단 바 (TopBar 컴포넌트 활용) */}
      <TopBar isLogo={false} page="챗봇" isWhite />

      {/* 챗봇 메시지 영역 */}
      <div className="px-6 pt-36 pb-20 bg-primary-light min-h-screen">
        <div className="flex flex-col space-y-4">
          {/* 사용자와 챗봇 메시지 목록 표시 */}
          {messages.map((msg) => (
            <div
              key={msg.key} // 고유한 key 사용
              className={`flex ${msg.type === "user" ? "justify-end" : "items-start"}`}
            >
              {/* 챗봇 메시지일 때만 프로필 이미지 추가 */}
              {msg.type === "bot" && (
                <img
                  src={bean}
                  alt="bot-profile"
                  className="w-8 h-8 rounded-full mr-2"
                />
              )}
              <div
                className={`text-sm shadow-lg mt-2 px-4 py-2 max-w-xs ${
                  msg.type === "user"
                    ? "bg-primary text-white rounded-tl-lg rounded-bl-lg rounded-br-lg"
                    : "bg-white text-gray-800 rounded-tr-lg rounded-bl-lg rounded-br-lg"
                }`}
              >
                {msg.content}
              </div>
            </div>
          ))}
          {/* 스크롤을 위한 빈 div */}
          <div ref={messagesEndRef} />
        </div>

        {/* 질문 옵션 버튼들 표시 */}
        <div className="flex flex-wrap gap-2 mt-2">
          {options.map((option) => (
            <button
              key={option.id} // 각 옵션 버튼에 고유한 key 설정
              type="button"
              className="bg-primary text-white text-xs px-4 py-2 rounded-xl shadow-sm hover:bg-primary-light hover:border border-white"
              onClick={() => {
                switch (mode) {
                  case "question":
                    handleOptionClick(option.id, option.description);
                    break;
                  case "currency":
                    handleCurrencyClick(option.id, option.description);
                    break;
                  default:
                    // 기본 동작이 필요하다면 작성 (없다면 생략 가능)
                    break;
                }
              }}
            >
              {option.description}
            </button>
          ))}
        </div>
      </div>

      {/* 사용자 텍스트 입력창 및 전송 버튼 */}
      <div className="w-[360px] h-[60px] flex fixed bottom-0 left-[calc(50%+10px)] transform translate-x-[-50%] p-2 bg-white shadow-2xl space-x-2 z-50">
        <input
          type="text"
          value={userInput}
          onChange={handleInputChange}
          className="flex-grow border border-gray-300 rounded-lg px-4 py-2 text-sm"
          placeholder="메시지를 입력하세요..."
        />
        <button
          type="button"
          className="bg-primary text-white px-4 py-2 rounded-lg shadow-sm"
          onClick={handleSendMessage}
        >
          전송
        </button>
      </div>
    </div>
  );
};

export default Chatbot;
