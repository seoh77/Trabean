// Front -> Travel
// 여행통장 조회 API
// /api/travel/{parentAccountId}
export interface TravelAccount {
  accountId: number;
  country: string;
  accountBalance: number;
  exchangeCurrency: string;
}

export interface TravelAccountData {
  accountId: number;
  accountNo: string;
  accountName: string;
  bankName: string;
  account: TravelAccount[];
}

// Front -> Travel
// 목표금액 전체 조회 API
// /api/travel/targetAmount/{accountId}
export interface TravelAccountMember {
  userId: number;
  userName: string;
  role: string;
  amount?: number;
}

export interface TravelAccountMemberAmountData {
  targetAmount: number;
  amount: number;
  memberList: TravelAccountMember[];
}

// Front -> Account
// 한화 여행통장 상세 조회 API
// /api/accounts/travel/domestic/{accountId}?startDate={startDate}&endDate={endDate}&transactionType={transactionType}
export interface TravelAccountMemberData {
  userId: number;
  memberCount: number;
  members: TravelAccountMember[];
}

// Front -> Account
// 한화 여행통장 상세 조회 API
// /api/accounts/travel/domestic/{accountId}?startDate={startDate}&endDate={endDate}&transactionType={transactionType}
export interface DomesticTravelAccountTransaction {
  transactionType: string;
  transactionSummary: string;
  transactionDate: string;
  transactionTime: string;
  transactionBalance: number;
  transactionAfterBalance: number;
  transactionMeno: string;
}

export interface DomesticTravelAccountDetailData {
  accountName: string;
  targetAmount: number;
  accountId: number;
  accountNo: string;
  accountBalance: number;
  bankName: string;
  transactionList: DomesticTravelAccountTransaction[];
}

// Front -> Travel
// 여행통장(외화) 상세 조회 API
// /api/travel/foreign/{accountId}?startDate={startDate}&endDate={endDate}&transactionType={transactionType}
export interface ForeignTravelAccountTransaction {
  transactionType: string;
  transactionSummary: string;
  transactionDate: string;
  transactionTime: string;
  transactionBalance: number;
  transactionAfterBalance: number;
  transactionMeno: string;
}

export interface ForeignTravelAccountDetailData {
  country: string;
  exchangeCurrency: string;
  accountBalance: number;
  list: ForeignTravelAccountTransaction[];
}
