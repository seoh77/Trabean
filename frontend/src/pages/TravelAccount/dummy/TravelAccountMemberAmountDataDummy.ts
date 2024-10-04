// 여행통장 멤버(userId = 14)가 한화 여행통장(accountId = 57)을 조회해서 해당 한화 여행통장의 멤버 정보를 받는 상황의 더미 데이터

import { TravelAccountMemberAmountData } from "../type/type";

const TravelAccountMemberAmountDataDummy: TravelAccountMemberAmountData = {
  targetAmount: 1000000,
  amount: 700000,
  memberList: [
    {
      userId: 14,
      userName: "카리나",
      role: "ADMIN",
      amount: 100000,
    },
    {
      userId: 13,
      userName: "장원영",
      role: "PAYER",
      amount: 100000,
    },
    {
      userId: 15,
      userName: "설윤",
      role: "NONE_PAYER",
      amount: 100000,
    },
    {
      userId: 16,
      userName: "장원영",
      role: "PAYER",
      amount: 100000,
    },
    {
      userId: 17,
      userName: "설윤",
      role: "NONE_PAYER",
      amount: 100000,
    },
  ],
};

export default TravelAccountMemberAmountDataDummy;
