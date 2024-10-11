import React, { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import client from "../../client";
import TopBar from "../../components/TopBar";
import CancelButton from "./CancelButton";
import ChatMessages from "./ChatMessages";
import ChatInput from "./ChatInput";
import ModalInput from "./ModalInput";
import OptionsPanel from "./OptionsPanel";
import OptionsSelect from "./OptionSelect";

interface Option {
  id: string;
  description: string;
}

interface Message {
  key: string;
  type: "user" | "bot";
  content: string;
}

let mode = "question";
let selectValue: unknown;

// exchange
let baseCurrency: unknown;
let targetCurrency: unknown;

// saving
let startDate: unknown;
let endDate: unknown;

// location
interface Location {
  country: string;
  city: string;
  days: number;
  trans: string;
  preferLoging: string;
  interest: string[];
  priority: string[];
  attractions: string[];
}

const Chatbot: React.FC = () => {
  const [options, setOptions] = useState<Option[]>([]);
  const [userInput, setUserInput] = useState("");
  const [inputValue, setInputValue] = useState<string | number>("");
  const [messages, setMessages] = useState<Message[]>([]);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const [step, setStep] = useState<number>(0);
  const [question, setQuestion] = useState<Option[]>([]);

  // 페이지 상태 관리
  const [sendState, setSendState] = useState<boolean>(true);
  const [enableCancel, setEnableCancel] = useState<boolean>(false);
  const [enableOK, setEnableOK] = useState<boolean>(false);
  const [isInputModal, setIsInputModal] = useState<boolean>(false);
  const [inputModalType, setInputModalType] = useState<string>("text");
  const [inputModalText, setInputModalText] = useState<string | number | null>(
    "",
  );
  const [inputModalHolder, setInputModalHolder] = useState<string | undefined>(
    "",
  );
  const [isSelectOption, setIsSelectOption] = useState<boolean>(false);
  const [selectedOptions, setSelectedOptions] = useState<Option[]>([]);

  // 장소 기능
  const [locationData, setLocationData] = useState<Location>({
    country: "",
    city: "",
    days: 0,
    trans: "",
    preferLoging: "",
    interest: [],
    priority: [],
    attractions: [],
  });
  let country = "";

  const navigate = useNavigate(); // useNavigate 훅 설정

  const currencyList = [
    { id: "KRW", description: "한국 (KRW)" },
    { id: "USD", description: "미국 (USD)" },
    { id: "EUR", description: "유럽 (EUR)" },
    { id: "JPY", description: "일본 (JPY)" },
    { id: "CNY", description: "중국 (CNY)" },
    { id: "GBP", description: "영국 (GBP)" },
    { id: "CHF", description: "스위스 (CHF)" },
    { id: "CAD", description: "캐나다 (CAD)" },
  ];

  // 질문 목록 받아오기
  useEffect(() => {
    setStep(0);
    const fetchChatbotData = async () => {
      try {
        const url = "/api/chatbot/start";
        const newResponse = await client().get(url);
        setQuestion(newResponse.data.options);
        setOptions(newResponse.data.options);
        setMessages((prev) => [
          ...prev,
          {
            key: `bot-${Date.now()}`,
            type: "bot",
            content: newResponse.data.message,
          },
        ]);
      } catch (error) {
        console.error("챗봇 데이터 가져오기 오류:", error);
      }
    };

    fetchChatbotData();
  }, []);

  // 메시지 추가하기
  const addMessage = (content: string, type: "user" | "bot" = "bot") => {
    setMessages((prev) => [
      ...prev,
      {
        key: `${type}-${Date.now()}-${Math.random()}`,
        type,
        content,
      },
    ]);
  };

  useEffect(() => {
    console.log("Options has been updated: ", options);
  }, [options]); // options가 변경될 때마다 실행

  // 환율 정보 받아오기
  const getExchange = async () => {
    try {
      // 요청 URL 및 Body 설정
      const url = "/api/travel/exchange/estimate";
      const convertedNumber = parseInt(
        String(inputValue).replace(/,/g, ""),
        10,
      );
      if (baseCurrency && targetCurrency) {
        const requestBody: {
          currency: string;
          exchangeCurrency: string;
          amount: number;
        } = {
          currency: String(targetCurrency),
          exchangeCurrency: String(baseCurrency),
          amount: convertedNumber,
        };
        const response = await client().post(url, requestBody);
        console.log("환전 결과 : ", response.data);
        return response.data;
      }
      // 계좌 개설 요청 전송
    } catch (error) {
      console.log(error);
      const response = {
        currency: {
          amount: "2,351,233",
          country: "한국",
          currency: "KRW",
        },
        exchangeCurrency: {
          amount: "12,445",
          country: "중국",
          currency: "CNY",
        },
      };
      return response;
      // return null;
    }
    return null;
  };

  // saving mode
  const getSavingMsg = (goalAmount: number): string => {
    const chatMessage = `총 목표 금액 : ${goalAmount}`;
    setMessages((prev) => [
      ...prev,
      {
        key: `bot-${Date.now()}-${Math.random()}`,
        type: "bot",
        content: chatMessage,
      },
    ]);

    if (startDate && endDate && goalAmount) {
      // 날짜 포맷: YYYY-MM-DD
      const start = new Date(String(startDate));
      const end = new Date(String(endDate));

      // 두 날짜 간의 차이를 일 단위로 계산
      const totalDays = Math.ceil(
        (end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24),
      );

      if (totalDays < 0) {
        return "시작일은 종료일 보다 앞서야 해요!";
      }
      // 주 수 계산 (총 일수를 7로 나눈 값)
      const totalWeeks = Math.ceil(totalDays / 7);

      // 월 수 계산 (시작 날짜와 끝 날짜의 연도 및 월을 기준으로 계산)
      const totalMonths =
        end.getMonth() -
        start.getMonth() +
        1 +
        (end.getFullYear() - start.getFullYear()) * 12;

      // 매일 저축해야 하는 금액
      const dailyAmount = goalAmount / totalDays;
      // 매주 저축해야 하는 금액
      const weeklyAmount = goalAmount / totalWeeks;
      // 매달 저축해야 하는 금액 (기간이 2개월 이상인 경우에만 계산)
      const monthlyAmount = goalAmount / totalMonths;

      // 포맷된 저축 금액 문자열 생성
      const formattedDaily = dailyAmount.toLocaleString(undefined, {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
      });
      const formattedWeekly = weeklyAmount.toLocaleString(undefined, {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
      });
      const formattedMonthly = monthlyAmount.toLocaleString(undefined, {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
      });

      // 결과 문자열 반환
      return `총 목표액: ${goalAmount.toLocaleString()}원
    ${totalDays}일동안 : 매일 ${formattedDaily}원
    ${totalDays >= 14 ? `${totalWeeks}주동안 : 매주 ${formattedWeekly}원` : ""}
    ${totalMonths >= 2 && totalDays >= 60 ? `${totalMonths}달동안 : 한 달에 ${formattedMonthly}원` : ""}`;
    }
    return "잘못된 입력값이 존재합니다. 다시 시도해주세요.";
  };

  const setInitState = () => {
    setSelectedOptions([]);
    setStep(0);
    mode = "question";
    setOptions(question);
    setEnableCancel(false);
    setEnableOK(false);
    setIsInputModal(false);
    setSendState(true);
    setInputValue("");
    startDate = undefined;
    setIsSelectOption(false);
  };

  // 취소 버튼 눌렀을 때
  const handleCancelClick = () => {
    setInitState();
    setOptions(question);
    const msg = "무엇을 도와드릴까요?";
    addMessage(msg, "bot");
  };

  const setExchangeDate = async () => {
    let chatMessage = "";
    switch (step) {
      case 0:
        chatMessage = "기준통화를 선택해주세요";
        setOptions(currencyList);
        setSendState(false);
        setEnableCancel(true);
        setStep(1);
        break;
      case 1:
        chatMessage = "대상통화를 선택해주세요";
        baseCurrency = String(selectValue);
        console.log("base : ", baseCurrency);
        setStep(2);
        break;
      case 2:
        chatMessage = "환전 금액을 입력해주세요";
        targetCurrency = String(selectValue);
        console.log("target : ", targetCurrency);
        setInputModalText(`${baseCurrency} → ${targetCurrency}`);
        setInputModalHolder("금액을 입력하세요");
        setIsInputModal(true);
        setInputModalType("text");
        setEnableOK(true);
        setStep(3);
        setOptions([]);
        break;
      case 3: {
        // 환전 데이터 다 받음
        const response = await getExchange();
        if (response && response.currency && response.exchangeCurrency) {
          // response가 유효할 때만 문자열을 생성
          addMessage("환전 결과는 다음과 같아요!", "bot");
          chatMessage = `${response.exchangeCurrency.country} : ${response.exchangeCurrency.amount}  ${response.exchangeCurrency.currency} \n ➡  ${response.currency.country} : ${response.currency.amount} ${response.currency.currency}`;
          setInitState();
        } else {
          // response가 유효하지 않으면 기본 메시지 설정
          chatMessage = "환전 정보를 가져오는 데 실패했습니다.";
        }
        break;
      }
      default:
        chatMessage = "문제가 발생했습니다. 처음부터 다시 시도해주세요.";
        setStep(0);
        setExchangeDate();
        break;
    }
    addMessage(chatMessage, "bot");
    mode = "exchange";
  };

  const setSavingData = () => {
    let chatMessage = "";
    mode = "saving";
    switch (step) {
      case 0:
        setOptions([]);
        chatMessage = "저축 목표 관리 정보를 제공해 드릴게요.";
        setInputModalText("저축 시작일을 입력해주세요");
        setInputModalHolder("YYYY-MM-DD");
        setInputModalType("date");
        setIsInputModal(true);
        setEnableOK(true);
        setEnableCancel(true);
        setStep(1);
        break;
      case 1:
        startDate = String(inputValue);
        chatMessage = `시작일 : ${inputValue}`;
        setInputModalText("저축 종료일을 입력해주세요");
        setInputModalHolder("YYYY-MM-DD");
        setInputModalType("date");
        setStep(2);
        break;
      case 2:
        endDate = String(inputValue);
        chatMessage = `종료일 : ${inputValue}`;
        setInputModalText("총 목표액을 입력해주세요");
        setInputValue("");
        setInputModalHolder("금액을 입력해주세요.");
        setInputModalType("text");
        setStep(3);
        break;
      case 3: {
        const amountValue = Number(String(inputValue).replace(/,/g, ""));
        console.log(amountValue);
        if (amountValue) {
          selectValue = amountValue;
          addMessage(getSavingMsg(Number(selectValue)));
          setInitState();
        } else {
          setInputModalText("숫자 형식의 금액을 입력해주세요");
        }
        break;
      }
      default:
        break;
    }
    if (chatMessage) {
      addMessage(chatMessage);
    }
  };

  // 옵션 선택 버튼
  const handleSelectClick = (id: string, description: string) => {
    const isAlreadySelected = selectedOptions.some(
      (option) => option.id === id,
    );

    let updatedSelectedOptions;
    if (isAlreadySelected) {
      // 이미 선택된 옵션이면 제거
      updatedSelectedOptions = selectedOptions.filter(
        (option) => option.id !== id,
      );
    } else {
      // 선택되지 않은 옵션이면 배열에 추가
      updatedSelectedOptions = [...selectedOptions, { id, description }];
    }

    setSelectedOptions(updatedSelectedOptions);
    console.log("Selected Options:", updatedSelectedOptions);
  };

  // options 배열을 변환하는 함수
  const transformOptions = (resOptions: string[]) =>
    resOptions.map((option: string) => ({
      id: option, // id에 option 값을 그대로 사용
      description: option, // description에 option 값을 그대로 사용
    }));

  const transformId = (resOptions: string[]) =>
    resOptions.map((option: string, index: number) => ({
      id: String(index + 1), // 배열 순서대로 1부터 id를 부여
      description: option, // description에 option 값을 그대로 사용
    }));

  useEffect(() => {
    console.log("Updated locationData: ", locationData);
  }, [locationData]);

  const updateLocationData = (
    key: keyof Location,
    value: string | number | string[],
  ) => {
    setLocationData((prev) => ({
      ...prev, // 기존의 locationData 필드들을 유지
      [key]: value, // 특정 필드만 업데이트
    }));
    console.log("locationData : ", locationData);
  };

  // lodcation 데이터 설정 함수
  const setAnswerLocationData = () => {
    switch (step) {
      case 1:
        country = String(selectValue);
        updateLocationData("country", String(selectValue));
        break;
      case 2:
        updateLocationData("city", String(selectValue));
        break;
      case 3:
        updateLocationData("days", Number(selectValue));
        break;
      case 4:
        updateLocationData("trans", String(selectValue));
        console.log("country : ", locationData);
        break;
      case 5:
        setSelectedOptions([]);
        updateLocationData("preferLoging", String(selectValue));
        break;
      case 6: {
        setEnableOK(false);
        const idArray = selectedOptions.map((option) => option.id);
        updateLocationData("attractions", idArray);
        setSelectedOptions([]);
        break;
      }
      case 7:
        break;
      default:
        console.log("Invalid day");
    }
  };

  const transformLocationData = () => ({
    location: {
      country: locationData.country,
      city: locationData.city,
    },
    days: locationData.days,
    transportation: locationData.trans,
    preferences: {
      interest: locationData.interest,
      priority: locationData.priority.map(Number), // 문자열 배열을 숫자 배열로 변환
      preferLoging: locationData.preferLoging,
    },
    attractions: locationData.attractions,
  });

  const transformLocationQuestion = () => ({
    country: locationData.country,
    city: locationData.city,
    days: locationData.days,
    trans: locationData.trans,
  });

  const setLocationOption = async () => {
    let chatMessage = "";
    let url = `/api/chatbot/location/${step}`;
    switch (step) {
      case 1: {
        url += `?country=${country}`;
        const responseStep1 = await client().get(url);
        chatMessage = responseStep1.data.question;
        setOptions(transformOptions(responseStep1.data.options));
        break;
      }

      case 2:
        {
          const responseStep2 = await client().get(url);
          chatMessage = responseStep2.data.question;
          const numberOfDays = responseStep2.data.options;
          const dayOptions = Array.from(
            { length: numberOfDays },
            (_, index) => ({
              id: String(index + 1),
              description: index === 0 ? "당일치기" : `${index + 1}일`, // 1일은 "당일치기", 그 외는 "n일" 형식
            }),
          );
          setOptions(dayOptions);
        }
        break;

      case 6: {
        const responseStep1 = await client().get(url);
        chatMessage = responseStep1.data.question;
        setOptions(transformId(responseStep1.data.options));
        console.log("priority!!! : ", transformId(responseStep1.data.options));
        break;
      }

      case 7: {
        const transformedDataStep7 = transformLocationQuestion();
        try {
          const responseStep7 = await client().post(url, transformedDataStep7); // 변환된 데이터 전송
          console.log("서버 응답:", responseStep7.data); // 성공 시 서버 응답 출력

          const transformedLocationsStep7 = responseStep7.data.options.map(
            (location: { id: string; name: string }) => ({
              id: location.id,
              description: location.name, // "name" 속성을 "description"으로 변경
            }),
          );
          setOptions(transformedLocationsStep7);
          chatMessage = responseStep7.data.question;
        } catch (error) {
          console.error("데이터 전송 중 오류 발생:", error);
        }
        break;
      }

      case 8: {
        const transformedData = transformLocationData();
        navigate("/chatbot/map", {
          // 지도 페이지로 값 전달
          state: { mapLocationData: transformedData },
        });
        // console.log("transformedData : ", transformedData);
        // url = "http://localhost:8082/api/chatbot/recommendLocation";
        // try {
        //   const responseStep8 = await axios.post(url, transformedData); // 변환된 데이터 전송
        //   console.log("서버 응답:", responseStep8.data); // 성공 시 서버 응답 출력
        //   chatMessage = responseStep8.data.question;
        // } catch (error) {
        //   console.error("데이터 전송 중 오류 발생:", error);
        // }
        setInitState();
        break;
      }

      default: {
        const responseDefault = await client().get(url);
        chatMessage = responseDefault.data.question;
        setOptions(transformOptions(responseDefault.data.options));
        break;
      }
    }

    if (step === 5) {
      setIsSelectOption(true);
    }
    if (chatMessage) {
      addMessage(chatMessage, "bot");
    }
    mode = "location";
    setStep(step + 1);
  };

  useEffect(() => {
    if (mode === "location") {
      if (step === 6) {
        if (selectedOptions.length >= 1) {
          setEnableOK(true);
        } else {
          setEnableOK(false);
        }
      } else if (step === 7) {
        if (selectedOptions.length === 4) {
          setEnableOK(true);
        } else {
          setEnableOK(false);
        }
      } else if (step === 8) {
        if (
          selectedOptions.length >= 1 &&
          selectedOptions.length <= locationData.days * 2
        ) {
          setEnableOK(true);
        } else {
          setEnableOK(false);
        }
      }
    }
  }, [locationData.days, selectedOptions, step]);

  // 확인 버튼을 눌렀을 때
  const handleOkayClick = async () => {
    setSelectedOptions([]);
    console.log(selectedOptions);
    if (mode === "location") {
      switch (step) {
        case 6:
          locationData.interest = selectedOptions.map(
            (option) => option.description,
          );
          break;
        case 7:
          locationData.priority = selectedOptions.map((option) => option.id);
          break;
        case 8:
          locationData.attractions = selectedOptions.map((option) => option.id);
          break;
        default:
          break;
      }
    }
    switch (mode) {
      case "location":
        setAnswerLocationData();
        setLocationOption();
        break;
      case "exchange":
        setExchangeDate();
        break;
      case "saving":
        setSavingData();
        break;
      default:
        break;
    }
  };

  // 옵션 핸들링
  const handleOptionClick = (id: string, description: string) => {
    selectValue = id;
    setEnableCancel(true);
    setSendState(false);
    addMessage(description, "user");
    if (step === 0) {
      // 모드 선택
      mode = id;
    }
    // 답변 선택
    switch (mode) {
      case "question":
        break;
      case "location":
        setAnswerLocationData();
        setLocationOption();
        break;
      case "exchange":
        setExchangeDate();
        break;
      case "saving":
        setSavingData();
        break;
      default:
        break;
    }
  };

  const handelSendMessage = async () => {
    setOptions([]);
    addMessage(userInput, "user");
    addMessage("· · · ·", "bot");
    setUserInput("");
    const url = "/api/chatbot/question";
    const requestBody = { question: String(userInput) };
    const response = await client().post(url, requestBody);
    if (response) {
      setInitState();
      setMessages((prevMessages) => [
        ...prevMessages.slice(0, -1), // 기존 배열의 마지막 요소를 제외한 나머지 요소를 복사
      ]);
      addMessage(response.data.answer, "bot");
    }
  };

  return (
    <div className="overflow-hidden min-h-screen">
      <TopBar isLogo={false} page="챗봇" isWhite />
      <div className="px-6 pt-36 pb-20 bg-primary-light min-h-screen">
        <ChatMessages messages={messages} messagesEndRef={messagesEndRef} />
        {!isInputModal && !isSelectOption && (
          <OptionsPanel options={options} onOptionClick={handleOptionClick} />
        )}
        {isSelectOption && (
          <OptionsSelect
            options={options}
            onOptionClick={(id: string, description: string) =>
              handleSelectClick(id, description)
            }
          />
        )}
        {isInputModal && (
          <ModalInput
            isAmount
            inputType={inputModalType}
            text={inputModalText}
            holderText={inputModalHolder}
            inputValue={inputValue} // 현재 inputValue 전달
            setInputValue={setInputValue} // inputValue 업데이트 함수 전달
            startDay={String(startDate)}
          />
        )}
        {(enableCancel || enableOK) && (
          <CancelButton
            onCancelClick={handleCancelClick}
            onOkayClick={handleOkayClick}
            cancle={enableCancel}
            ok={enableOK}
          />
        )}
      </div>
      <ChatInput
        userInput={userInput}
        onInputChange={(e) => setUserInput(e.target.value)}
        onSendMessage={handelSendMessage}
        sendDisable={!sendState}
      />
    </div>
  );
};

export default Chatbot;
