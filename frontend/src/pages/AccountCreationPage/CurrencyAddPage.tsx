import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import NavBar from "./NavBar";

// 국기 이미지 파일 import
import usdFlag from "../../assets/flagIcon/usd.png";
import eurFlag from "../../assets/flagIcon/eur.png";
import gbpFlag from "../../assets/flagIcon/gbp.png";
import cadFlag from "../../assets/flagIcon/cad.png";
import chfFlag from "../../assets/flagIcon/chf.png";
import cnyFlag from "../../assets/flagIcon/cny.png";
import jpyFlag from "../../assets/flagIcon/jpy.png";

const CurrencySelectionPage: React.FC = () => {
  const navigate = useNavigate(); // 페이지 이동을 위한 navigate 훅
  const [selectedCurrency, setSelectedCurrency] = useState<string | null>(null); // 선택된 통화 상태

  // 통화 목록 데이터
  const currencyList = [
    { name: "미국 달러 USD", value: "USD", img: usdFlag },
    { name: "유럽 유로 EUR", value: "EUR", img: eurFlag },
    { name: "영국 파운드 GBP", value: "GBP", img: gbpFlag },
    { name: "캐나다 달러 CAD", value: "CAD", img: cadFlag },
    { name: "스위스 프랑 CHF", value: "CHF", img: chfFlag },
    { name: "중국 위안화 CNY", value: "CNY", img: cnyFlag },
    { name: "일본 엔화 JPY", value: "JPY", img: jpyFlag },
  ];

  // 통화 항목 클릭 핸들러
  const handleCurrencySelect = (value: string) => {
    setSelectedCurrency(value);
  };

  // 다음 단계 이동 핸들러
  const handleNextStep = () => {
    if (selectedCurrency) {
      // 선택된 통화 정보와 함께 다음 페이지로 이동
      navigate("/creation/travel/next-step", { state: { selectedCurrency } });
    }
  };

  return (
    <div className="px-6 py-8 bg-gray-50 min-h-screen">
      <NavBar text="외화 추가" />

      {/* 상단 텍스트 */}
      <div className="text-center mt-4 mb-6">
        <h1 className="text-lg font-bold">
          추가하고자 하는 통화를 선택해주세요
        </h1>
        <p className="text-sm text-gray-500">
          * 이미 등록된 통화는 중복으로 추가할 수 없어요
        </p>
      </div>

      {/* 통화 목록 */}
      <div className="flex flex-col space-y-4">
        {currencyList.map((currency) => (
          <button
            type="button"
            key={currency.value}
            onClick={() => handleCurrencySelect(currency.value)}
            className={`flex items-center justify-between p-4 bg-white rounded-lg shadow-md border ${
              selectedCurrency === currency.value
                ? "border-green-500"
                : "border-transparent"
            }`}
          >
            <div className="flex items-center">
              <img
                src={currency.img}
                alt={currency.name}
                className="w-10 h-10 mr-4 rounded-full"
              />
              <span className="text-md font-medium">{currency.name}</span>
            </div>
            {selectedCurrency === currency.value && (
              <span className="text-green-500 font-bold">✔️</span>
            )}
          </button>
        ))}
      </div>

      {/* 다음 단계 버튼 */}
      <div className="flex justify-center mt-10">
        <button
          type="button"
          onClick={handleNextStep}
          className={`px-8 py-4 rounded-full text-lg font-bold text-white ${
            selectedCurrency ? "bg-green-500" : "bg-gray-300"
          }`}
          disabled={!selectedCurrency}
        >
          다음 단계
        </button>
      </div>
    </div>
  );
};

export default CurrencySelectionPage;
