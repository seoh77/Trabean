import React, { useEffect, useState } from "react";

import { useParams } from "react-router-dom";

import bean from "../../../assets/bean.png";

import NavBar from "../component/NavBar";
import tmpMemberData from "../dummy/TravelAccountMemberDataDummy";
import tmpAccountData from "../dummy/TravelAccountDataDummy";
import ChangeUserRoleModal from "../ChangeUserRoleModal";

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

const MemberManagementPage: React.FC = () => {
  const { accountId } = useParams<{ accountId: string }>();

  const [accountData, setAccountData] = useState<AccountData>();
  const [memberData, setMemberData] = useState<MemberData>();

  // 결제 권한 변경 모달
  const [isChangeUserRoleModalOpen, setIsChangeUserRoleModalOpen] =
    useState(false);

  // 모달의 데이터
  const [memberInfo, setMemberInfo] = useState<Member>();

  const openChangeUserRoleModal = (member: Member) => {
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
    setAccountData(tmpAccountData);
  }, []);

  // 여행통장 멤버 정보를 받는 fetch 요청
  useEffect(() => {
    console.log(accountId);
    setMemberData(tmpMemberData);
  }, [accountId]);

  return (
    <div
      className={`h-full relative ${isChangeUserRoleModalOpen ? "bg-gray-300" : ""}`}
    >
      {/* 네비게이션 바 */}
      <div className="px-4 py-2">
        <NavBar text="멤버 관리" />
      </div>

      {/* 통장 정보 */}
      <div className="px-4 py-2">
        <div className="flex">
          <img src={bean} alt="bean" className="w-10 h-10" />
          <div>
            <div className="font-bold text-lg">{accountData?.accountName}</div>
            <div>
              {accountData?.bankName} {accountData?.accountNo}
            </div>
          </div>
        </div>
      </div>

      {/* 멤버 정보 */}
      <div className="px-4 py-2">
        <div className="text-xs">{memberData?.memberCount}명</div>
        <div>모임원</div>
      </div>

      {/* 멤버 목록 */}
      <div className="px-4 py-2">
        <div>
          {memberData?.members.map((member) => {
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
