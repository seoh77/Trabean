// 한화 여행통장(accountId = 58)을 조회해서 해당 한화 여행통장과 연결된 외화 여행통장들을 받는 상황의 더미 데이터

import { TravelAccountData } from "../type/type";

const TravelAccountDataDummy: TravelAccountData = {
  accountId: 57,
  accountNo: "1000-1234-1245",
  accountName: "카리나와 도쿄 대모험",
  bankName: "트래빈 은행",
  account: [
    {
      accountId: 58,
      country: "대한민국",
      accountBalance: 10000000,
      exchangeCurrency: "KRW",
    },
    {
      accountId: 64,
      country: "미국",
      accountBalance: 800,
      exchangeCurrency: "USD",
    },
    {
      accountId: 67,
      country: "일본",
      accountBalance: 2000,
      exchangeCurrency: "JPY",
    },
  ],
};

export default TravelAccountDataDummy;
