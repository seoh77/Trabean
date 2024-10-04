import React from "react";

const ExchangeRates = () => {
  const mainCurrencies = [
    {
      id: 1,
      name: "미국 USD",
      rate: "1,239.72 KRW",
      change: "0.90",
      flag: "🇺🇸",
    },
    {
      id: 2,
      name: "유럽 EUR",
      rate: "1,351.98 KRW",
      change: "34.00",
      flag: "🇪🇺",
    },
    {
      id: 3,
      name: "일본 JPY",
      rate: "1,000.06 KRW",
      change: "0.05",
      flag: "🇯🇵",
    },
  ];

  const otherCurrencies = [
    {
      id: 1,
      name: "중국 CNY",
      rate: "200.72 KRW",
      change: "34.00",
      flag: "🇨🇳",
    },
    {
      id: 2,
      name: "영국 GBP",
      rate: "1,622.69 KRW",
      change: "34.00",
      flag: "🇬🇧",
    },
    {
      id: 3,
      name: "스위스 CHF",
      rate: "1,332.03 KRW",
      change: "34.00",
      flag: "🇨🇭",
    },
    {
      id: 4,
      name: "캐나다 CAD",
      rate: "993.28 KRW",
      change: "34.00",
      flag: "🇨🇦",
    },
  ];

  return (
    <div className="p-4">
      {/* 상단 제목 */}
      <div className="flex items-center mb-6">
        <button type="button" className="mr-4 text-lg">
          ←
        </button>
        {/* 뒤로가기 버튼 */}
        <h1 className="text-2xl font-bold">환율 조회</h1>
      </div>

      {/* 주요 통화 */}
      <div className="mb-6">
        <div className="flex justify-between items-center mb-2">
          <h2 className="text-lg font-semibold">주요 통화</h2>
          <div className="w-full border-b border-black ml-2" />
        </div>
        {mainCurrencies.map((currency) => (
          <div
            key={currency.id}
            className="flex justify-between items-center mb-4"
          >
            <div className="flex items-center">
              <span className="text-2xl mr-4">{currency.flag}</span>
              <p>{currency.name}</p>
            </div>
            <div className="text-right">
              <p>{currency.rate}</p>
              <p className="text-sm text-red-500">▲ {currency.change}</p>
            </div>
          </div>
        ))}
      </div>

      {/* 기타 통화 */}
      <div>
        <div className="flex justify-between items-center mb-2">
          <h2 className="text-lg font-semibold">기타 통화</h2>
          <div className="w-full border-b border-black ml-2" />
        </div>
        {otherCurrencies.map((currency) => (
          <div
            key={currency.id}
            className="flex justify-between items-center mb-4"
          >
            <div className="flex items-center">
              <span className="text-2xl mr-4">{currency.flag}</span>
              <p>{currency.name}</p>
            </div>
            <div className="text-right">
              <p>{currency.rate}</p>
              <p className="text-sm text-red-500">▲ {currency.change}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default ExchangeRates;
