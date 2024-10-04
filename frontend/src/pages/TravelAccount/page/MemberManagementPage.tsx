import React, { useEffect, useState } from "react";

import { useParams } from "react-router-dom";

import bean from "../../../assets/bean.png";

import TopBar from "../../../components/TopBar";
import ChangeUserRoleModal from "../ChangeUserRoleModal";
import {
  TravelAccountData,
  TravelAccountMember,
  TravelAccountMemberData,
} from "../type/type";
import TravelAccountDataDummy from "../dummy/TravelAccountDataDummy";
import TravelAccountMemberDataDummy from "../dummy/TravelAccountMemberDataDummy";

const MemberManagementPage: React.FC = () => {
  const { accountId } = useParams<{ accountId: string }>();

  const [travelAccountData, setTravelAccountData] =
    useState<TravelAccountData>();
  const [travelAccountMemberData, setTravelAccountMemberData] =
    useState<TravelAccountMemberData>();

  // 결제 권한 변경 모달
  const [isChangeUserRoleModalOpen, setIsChangeUserRoleModalOpen] =
    useState(false);

  // 모달의 데이터
  const [memberInfo, setMemberInfo] = useState<TravelAccountMember>();

  const openChangeUserRoleModal = (member: TravelAccountMember) => {
    setIsChangeUserRoleModalOpen(true);
    setMemberInfo(member);
  };

  const closeChangeUserRoleModal = () => {
    setIsChangeUserRoleModalOpen(false);
  };

  const formatUserRole = (role: string) => {
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

  // 여행통장 계좌 정보를 받는 fetch 요청
  useEffect(() => {
    setTravelAccountData(TravelAccountDataDummy);
  }, []);

  // 여행통장 멤버 정보를 받는 fetch 요청
  useEffect(() => {
    console.log(accountId);
    setTravelAccountMemberData(TravelAccountMemberDataDummy);
  }, [accountId]);

  return (
    <div
      className={`h-full relative ${isChangeUserRoleModalOpen ? "bg-gray-300" : ""}`}
    >
      {/* 네비게이션 바 */}
      <div className="px-4 py-2 pt-16">
        <TopBar page="멤버 관리" isWhite isLogo={false} />
      </div>

      {/* 통장 정보 */}
      <div className="px-4 py-2">
        <div className="flex">
          <img src={bean} alt="bean" className="w-10 h-10" />
          <div>
            <div className="font-bold text-lg">
              {travelAccountData?.accountName}
            </div>
            <div>
              {travelAccountData?.bankName} {travelAccountData?.accountNo}
            </div>
          </div>
        </div>
      </div>

      {/* 멤버 정보 */}
      <div className="px-4 py-2">
        <div className="text-xs">{travelAccountMemberData?.memberCount}명</div>
        <div>모임원</div>
      </div>

      {/* 멤버 목록 */}
      <div className="px-4 py-2">
        <div>
          {travelAccountMemberData?.members.map((member) => {
            const roleText = formatUserRole(member.role);
            const isNonPayer = roleText === "결제불가";
            return (
              <button
                type="button"
                key={member.userId}
                className="flex py-4"
                onClick={() => openChangeUserRoleModal(member)}
              >
                <img src={bean} alt="bean" className="w-10 h-10" />
                <div>
                  <div>{member.userName}</div>
                  <div
                    className={`text-xs ${isNonPayer ? "text-red-500" : ""}`}
                  >
                    {roleText}
                  </div>
                </div>
              </button>
            );
          })}
        </div>
      </div>

      {/* 유저 권한 변경 모달 */}
      {isChangeUserRoleModalOpen && memberInfo ? (
        <div className="absolute bottom-0 left-0 w-full">
          <ChangeUserRoleModal
            memberInfo={memberInfo}
            onClose={closeChangeUserRoleModal}
          />
        </div>
      ) : null}
    </div>
  );
};

export default MemberManagementPage;
