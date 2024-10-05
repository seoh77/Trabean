import beanAdmin from "../../../assets/bean_admin.png";
import beanPayer from "../../../assets/bean_on.png";
import beanNonePayer from "../../../assets/bean_off.png";

import cad from "../../../assets/flagIcon/cad.png";
import chf from "../../../assets/flagIcon/chf.png";
import cny from "../../../assets/flagIcon/cny.png";
import eur from "../../../assets/flagIcon/eur.png";
import gbp from "../../../assets/flagIcon/gbp.png";
import jpy from "../../../assets/flagIcon/jpy.png";
import krw from "../../../assets/flagIcon/krw.png";
import usd from "../../../assets/flagIcon/usd.png";

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

export function getCurrencySymbol(currencyCode: string): string {
  return currencySymbols[currencyCode] || ""; // 없는 통화일 경우 빈 문자열 반환
}

export const getBeanImage = (role: string) => {
  switch (role) {
    case "PAYER":
      return beanPayer;
    case "NONE_PAYER":
      return beanNonePayer;
    default:
      return beanAdmin;
  }
};

export const formatUserRole = (role: string) => {
  switch (role) {
    case "ADMIN":
      return "모임장";
    case "PAYER":
      return "결제가능";
    case "NONE_PAYER":
      return "결제불가";
    default:
      return "";
  }
};

export const getCurrencyImage = (exchangeCurrency: string) => {
  switch (exchangeCurrency) {
    case "USD":
      return usd;
    case "EUR":
      return eur;
    case "GBP":
      return gbp;
    case "JPY":
      return jpy;
    case "CNY":
      return cny;
    case "KRW":
      return krw;
    case "CAD":
      return cad;
    default:
      return chf;
  }
};
