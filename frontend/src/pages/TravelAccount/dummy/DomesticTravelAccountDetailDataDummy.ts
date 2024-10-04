// 한화 여행통장(accountId = 58)을 조회해서 해당 한화 여행통장과 연결된 외화 여행통장들을 받는 상황의 더미 데이터

import { DomesticTravelAccountDetailData } from "../type/type";

const DomesticTravelAccountDetailDataDummy: DomesticTravelAccountDetailData = {
  accountName: "까비와 함께하는 세계일주",
  targetAmount: 10000000,
  accountId: 57,
  accountNo: "1002-555-139750",
  accountBalance: 1000000,
  bankName: "트래빈 뱅크",
  transactionList: [
    {
      transactionType: "1",
      transactionSummary: "닝닝",
      transactionDate: "20240404",
      transactionTime: "121212",
      transactionBalance: 12,
      transactionAfterBalance: 30,
      transactionMeno: "15",
    },
    {
      transactionType: "1",
      transactionSummary: "카리나",
      transactionDate: "20240403",
      transactionTime: "121212",
      transactionBalance: 15,
      transactionAfterBalance: 18,
      transactionMeno: "16",
    },
    {
      transactionType: "2",
      transactionSummary: "OO식당",
      transactionDate: "20240402",
      transactionTime: "121212",
      transactionBalance: 2,
      transactionAfterBalance: 3,
      transactionMeno: "15",
    },
    {
      transactionType: "1",
      transactionSummary: "육민우",
      transactionDate: "20240401",
      transactionTime: "121212",
      transactionBalance: 5,
      transactionAfterBalance: 5,
      transactionMeno: "14",
    },
  ],
};

export default DomesticTravelAccountDetailDataDummy;
