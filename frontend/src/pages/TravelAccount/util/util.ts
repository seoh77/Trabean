const currencySymbols: { [key: string]: string } = {
  KRW: "₩", // 한국 원화
  USD: "$", // 미국 달러
  EUR: "€", // 유로
  JPY: "¥", // 일본 엔화
  CNY: "¥", // 중국 위안화
  GBP: "£", // 영국 파운드
  CHF: "₣", // 스위스 프랑
  CAD: "C$", // 캐나다 달러
};

export default function getCurrencySymbol(currencyCode: string): string {
  return currencySymbols[currencyCode] || ""; // 없는 통화일 경우 빈 문자열 반환
}
