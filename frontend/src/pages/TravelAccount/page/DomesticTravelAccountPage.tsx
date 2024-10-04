import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import bean from "../../../assets/bean.png";
import TopBar from "../../../components/TopBar";
import Alarm from "../component/Alarm";
import TargetAmountProgressBar from "../component/TargetAmountProgressBar";
import ChatBot from "../component/ChatBot";
import ChangeTargetAmountModal from "../ChangeTargetAmountModal";
import getCurrencySymbol from "../util/util";
import { TravelAccountData, TravelAccountMemberAmountData } from "../type/type";
import client from "../../../client";
import Loading from "../component/Loading";

const DomesticTravelAccountPage: React.FC = () => {
  const { parentAccountId } = useParams();

  const nav = useNavigate();

  const [loading1, setLoading1] = useState(true);
  const [loading2, setLoading2] = useState(true);

  // 여행통장 상태관리
  const [travelAccountData, setTravelAccountData] =
    useState<TravelAccountData>();

  // 여행통장 멤버 상태관리
  const [travelAccountMemberAmountData, setTravelAccountMemberAmountData] =
    useState<TravelAccountMemberAmountData>();

  // 목표 금액과 현재 모인 금액 상태 상태관리
  const [targetAmount, setargetAmount] = useState<number>(0);
  const [collectedAmount, setCollectedAmount] = useState<number>(0);

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

  // Account 서버 여행통장 조회 API fetch 요청
  useEffect(() => {
    const token = "";
    const getTravelAccountData = async () => {
      const response = await client(token).get(
        `/api/travel/${parentAccountId}`,
      );
      setTravelAccountData(response.data);
      setLoading1(false);
    };

    if (parentAccountId) {
      getTravelAccountData();
    }
  }, [parentAccountId]);

  // Trabean 서버 목표금액 전체 조회 (role 추가하기) API fetch 요청
  useEffect(() => {
    const token = "";
    const getTravelAccountMemberAmountData = async () => {
      const response = await client(token).get(
        `/api/travel/targetAmount/${parentAccountId}`,
      );
      setTravelAccountMemberAmountData(response.data);
      setargetAmount(response.data.targetAmount);
      setCollectedAmount(response.data.amount);
      setLoading2(false);
    };

    if (parentAccountId) {
      getTravelAccountMemberAmountData();
    }
  }, [parentAccountId]);

  // 로딩 중이면 로딩 스피너 표시
  if (loading1 || loading2) {
    return <Loading />;
  }

  return (
    <div className="h-full relative bg-zinc-100">
      {/* 네비게이션 바 */}
      <div className="pt-16">
        <TopBar isWhite={false} isLogo />
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
            <div className="text-lg font-bold">
              {travelAccountData?.accountName}
            </div>
            <div>
              <button type="button" onClick={handleUpdateTravelAccountInfo}>
                🍳
              </button>
            </div>
          </div>

          {/* 여행통장 목록 중단 */}
          <div className="py-4">
            {travelAccountData?.account.map((account) => (
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
              <div className="text-xs">₩{collectedAmount.toLocaleString()}</div>
              <div className="text-xs">₩{targetAmount.toLocaleString()}</div>
            </div>
          </div>
          <div className="flex flex-wrap justify-center">
            {travelAccountMemberAmountData?.memberList.map((member) => (
              <div
                key={member.userId}
                className="flex flex-col items-center p-2"
              >
                <img src={bean} alt="bean" className="w-10 h-10" />
                <div className="text-xs">{member.userName}</div>
                <div className="text-xs">
                  ₩{member.amount?.toLocaleString()}
                </div>
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

export default DomesticTravelAccountPage;
