import React, { useEffect, useState } from "react";

import { useNavigate } from "react-router-dom";

import bean from "../../assets/bean.png";

import NavBar from "./NavBar";
import Alarm from "./Alarm";
import TargetAmountProgressBar from "./TargetAmountProgressBar";
import ChatBot from "./ChatBot";
import ChangeTargetAmountModal from "./ChangeTargetAmountModal";

import tmpAccountData from "./constants/AccountDto";
import tmpMemberData from "./constants/MemberDto";

interface Account {
  accountId: number;
  country: string;
  accountBalance: number;
  exchangeCurrency: string;
}

interface AccountData {
  accountId: number;
  accountNo: string;
  accountName: string;
  bankName: string;
  account: Account[];
}

interface Member {
  userId: number;
  userName: string;
  role: string;
}

interface MemberData {
  memberCount: number;
  members: Member[];
}

const TravelAccountPage: React.FC = () => {
  const [accountData, setAccountData] = useState<AccountData>();
  const [memberData, setMemberData] = useState<MemberData>();

  // 통화 기호 매핑
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

  const getCurrencySymbol = (currencyCode: string): string =>
    currencySymbols[currencyCode] || currencyCode;

  // 목표 금액과 현재 모인 금액 상태
  const [targetAmount, setargetAmount] = useState(0);
  const [collectedAmount, setCollectedAmount] = useState(0);

  const nav = useNavigate();

  // 목표 관리 모달
  const [isChangeTargetAmountModalOpen, setIsChangeTargetAmountModalOpen] =
    useState(false);

  const openChangeTargetAmountModal = () =>
    setIsChangeTargetAmountModalOpen(true);
  const closeChangeTargetAmountModal = () =>
    setIsChangeTargetAmountModalOpen(false);

  const handleUpdateTravelAccountInfo = () => {
    console.log("여행통장 정보 수정 누름");
  };

  const handleCreateForeignAccount = () => {
    console.log("외화 추가하기 누름");
  };

  const handleNBbang = () => {
    console.log("친구들과 N빵하기 누름");
  };

  const handlePayment = () => {
    console.log("다함께 결제해요 누름");
  };

  const handleExpenseTracker = () => {
    console.log("예산관리 가계부 누름");
  };

  // 여행통장 계좌 정보를 받는 fetch 요청
  useEffect(() => {
    setAccountData(tmpAccountData);
    setargetAmount(1000000);
    setCollectedAmount(700000);
  }, []);

  // 여행통장 멤버 정보를 받는 fetch 요청
  useEffect(() => {
    setMemberData(tmpMemberData);
  }, []);

  return (
    <div className="h-full relative bg-zinc-100">
      {/* 네비게이션 바 */}
      <div className="px-4 py-2">
        <NavBar />
      </div>

      {/* 알림 */}
      <div className="px-4 py-2">
        <Alarm />
      </div>

      {/* 여행통장 목록 */}
      <div className="px-4 py-2">
        <div className="rounded-2xl p-4 bg-white">
          {/* 여행통장 목록 상단 */}
          <div className="flex justify-between p-2">
            <div className="text-lg font-bold">{accountData?.accountName}</div>
            <div>
              <button type="button" onClick={handleUpdateTravelAccountInfo}>
                🍳
              </button>
            </div>
          </div>

          {/* 여행통장 목록 중단 */}
          <div className="py-4">
            {accountData?.account.map((account) => (
              <div
                key={account.accountId}
                className="flex justify-between items-center p-2"
              >
                <img src={bean} alt="bean" className="w-6 h-6" />
                <div className="flex-grow font-bold ml-2">
                  {account.country}
                </div>
                <div className="font-bold">
                  {getCurrencySymbol(account.exchangeCurrency)}
                  {account.accountBalance.toLocaleString()}
                </div>
              </div>
            ))}
          </div>

          {/* 여행통장 목록 하단 */}
          <div>
            <button
              type="button"
              onClick={handleCreateForeignAccount}
              className="btn-lg w-[90%] mx-auto block"
            >
              외화 추가하기
            </button>
          </div>
        </div>
      </div>

      {/* 여행통장 멤버 목록 */}
      <div className="px-4 py-2">
        <div className="rounded-2xl p-4 bg-white">
          <div className="flex justify-end">
            <div className="text-right text-xs mr-2">
              <button type="button" onClick={openChangeTargetAmountModal}>
                목표관리
              </button>
            </div>
            <div className="text-right text-xs">
              <button
                type="button"
                onClick={() => nav("/accounts/travel/domestic/57/members")}
              >
                멤버관리
              </button>
            </div>
          </div>
          <div className="py-4">
            <TargetAmountProgressBar
              targetAmount={targetAmount}
              collectedAmount={collectedAmount}
            />
            <div className="flex justify-between">
              <div className="text-xs">{collectedAmount.toLocaleString()}</div>
              <div className="text-xs">{targetAmount.toLocaleString()}</div>
            </div>
          </div>
          <div className="flex flex-wrap justify-center">
            {memberData?.members.map((member) => (
              <div
                key={member.userId}
                className="flex flex-col items-center p-2"
              >
                <img src={bean} alt="bean" className="w-10 h-10" />
                <div className="text-xs">{member.userName}</div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* 여행통장 기능 목록 */}
      <div className="px-4 py-2">
        <div className="flex justify-between rounded-2xl">
          {/* 친구들과 N빵하기 */}
          <div>
            <button
              type="button"
              onClick={handleNBbang}
              className="flex flex-col items-center bg-white rounded-3xl px-6 py-2"
            >
              <div>
                <img src={bean} alt="bean" className="w-10 h-10" />
              </div>
              <div className="text-sm">친구들과</div>
              <div className="text-sm font-bold">N빵하기</div>
            </button>
          </div>
          {/* 다함께 결제해요 */}
          <div>
            <button
              type="button"
              onClick={handlePayment}
              className="flex flex-col items-center bg-white rounded-3xl px-6 py-2"
            >
              <div>
                <img src={bean} alt="bean" className="w-10 h-10" />
              </div>
              <div className="text-sm">다함께</div>
              <div className="text-sm font-bold">결제해요</div>
            </button>
          </div>
          {/* 예산관리 가계부 */}
          <div>
            <button
              type="button"
              onClick={handleExpenseTracker}
              className="flex flex-col items-center bg-white rounded-3xl px-6 py-2"
            >
              <div>
                <img src={bean} alt="bean" className="w-10 h-10" />
              </div>
              <div className="text-sm">예산관리</div>
              <div className="text-sm font-bold">가계부</div>
            </button>
          </div>
        </div>
      </div>

      {/* 챗봇 */}
      <div className="px-4 py-2">
        <ChatBot />
      </div>

      {/* 목표 관리 모달 */}
      {isChangeTargetAmountModalOpen ? (
        <div className="absolute bottom-0 left-0 w-full">
          <ChangeTargetAmountModal onClose={closeChangeTargetAmountModal} />
        </div>
      ) : null}
    </div>
  );
};

export default TravelAccountPage;
