import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

// 국기 이미지 파일 import
import usdFlag from "../../assets/flagIcon/usd.png";
import eurFlag from "../../assets/flagIcon/eur.png";
import gbpFlag from "../../assets/flagIcon/gbp.png";
import cadFlag from "../../assets/flagIcon/cad.png";
import chfFlag from "../../assets/flagIcon/chf.png";
import cnyFlag from "../../assets/flagIcon/cny.png";
import jpyFlag from "../../assets/flagIcon/jpy.png";
import TopBar from "../../components/TopBar";
import client from "../../client";

interface CurrencyData {
  id: number; // 나라 ID
  country: string; // 나라명
  currency: string; // 화폐
  exchangeRate: number; // 현재 환율
  pastExchangeRate: number; // 하루 전 환율
  changeRate: string; // 증감률
}

const ExchangeRates = () => {
  const { accountId } = useParams(); // Path Variable
  const [currencies, setCurrencies] = useState<CurrencyData[]>([]);

  useEffect(() => {
    // API에서 데이터를 불러오는 함수
    const fetchData = async () => {
      try {
        const response = await client().get("/api/travel/exchangeRate");
        setCurrencies(response.data); // 데이터를 상태에 저장
      } catch (error) {
        console.error("데이터를 불러오는데 실패했습니다.", error);
      }
    };

    fetchData();
  }, []);

  // 주요 통화 필터링
  const mainCurrencies = currencies.filter((currency) =>
    ["미국", "유럽", "일본"].includes(currency.country),
  );

  // 기타 통화 필터링
  const otherCurrencies = currencies.filter((currency) =>
    ["중국", "영국", "스위스", "캐나다"].includes(currency.country),
  );

  // 국기 이미지를 결정하는 함수
  const getFlagImage = (country: string): string => {
    if (country === "미국") return usdFlag;
    if (country === "유럽") return eurFlag;
    if (country === "일본") return jpyFlag;
    if (country === "중국") return cnyFlag;
    if (country === "영국") return gbpFlag;
    if (country === "스위스") return chfFlag;
    if (country === "캐나다") return cadFlag;
    return ""; // 기본값, 필요한 경우 다른 이미지 경로를 설정할 수 있음
  };

  return (
    <div className="p-4">
      {/* 상단 제목 */}
      <TopBar
        isWhite
        isLogo={false}
        page="환율 조회"
        path={`/accounts/travel/domestic/${accountId}`}
      />
      <div className="flex items-center mb-6">
        <h1 className="text-2xl font-bold">환율 조회</h1>
      </div>

      {/* 주요 통화 */}
      <div className="mb-6">
        <h2 className="text-lg font-semibold mb-2">주요 통화</h2>
        <div className="border-b border-black mb-4" />
        {mainCurrencies.map((currency) => (
          <div
            key={currency.id}
            className="flex justify-between items-center mb-4 p-2 border rounded-lg shadow-sm"
          >
            <div className="flex items-center">
              <img
                src={getFlagImage(currency.country)} // 국기 이미지 경로 사용
                alt={`${currency.country} 국기`}
                className="w-6 h-6 mr-2"
              />
              <p className="font-medium">
                {currency.country} {currency.currency}
              </p>
            </div>
            <div className="text-right">
              <p>{currency.exchangeRate.toLocaleString()} KRW</p>
              <p
                className={`text-sm ${
                  parseFloat(currency.changeRate) > 0
                    ? "text-red-500"
                    : "text-blue-500"
                }`}
              >
                {parseFloat(currency.changeRate) > 0 ? "▲" : "▼"}{" "}
                {currency.changeRate}
              </p>
            </div>
          </div>
        ))}
      </div>

      {/* 기타 통화 */}
      <div>
        <h2 className="text-lg font-semibold mb-2">기타 통화</h2>
        <div className="border-b border-black mb-4" />
        {otherCurrencies.map((currency) => (
          <div
            key={currency.id}
            className="flex justify-between items-center mb-4 p-2 border rounded-lg shadow-sm"
          >
            <div className="flex items-center">
              <img
                src={getFlagImage(currency.country)} // 국기 이미지 경로 사용
                alt={`${currency.country} 국기`}
                className="w-6 h-6 mr-2"
              />
              <p className="font-medium">
                {currency.country} {currency.currency}
              </p>
            </div>
            <div className="text-right">
              <p>{currency.exchangeRate.toLocaleString()} KRW</p>
              <p
                className={`text-sm ${
                  parseFloat(currency.changeRate) > 0
                    ? "text-red-500"
                    : "text-blue-500"
                }`}
              >
                {parseFloat(currency.changeRate) > 0 ? "▲" : "▼"}{" "}
                {currency.changeRate}
              </p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default ExchangeRates;
