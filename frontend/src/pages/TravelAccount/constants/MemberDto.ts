interface Member {
  userId: number;
  userName: string;
  role: string;
}

interface MemberData {
  memberCount: number;
  members: Member[];
}

// 여행통장 멤버(userId = 14)가 한화 여행통장(accountId = 57)을 조회해서 해당 한화 여행통장의 멤버 정보를 받는 상황의 더미 데이터

const tmpMemberData: MemberData = {
  memberCount: 2,
  members: [
    {
      userId: 14,
      userName: "카리나",
      role: "ADMIN",
    },
    {
      userId: 13,
      userName: "장원영",
      role: "PAYER",
    },
    {
      userId: 15,
      userName: "설윤",
      role: "NONE_PAYER",
    },
    {
      userId: 16,
      userName: "장원영",
      role: "PAYER",
    },
    {
      userId: 17,
      userName: "설윤",
      role: "NONE_PAYER",
    },
  ],
};

export default tmpMemberData;
