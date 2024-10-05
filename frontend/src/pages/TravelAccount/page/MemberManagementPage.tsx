import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import account from "../../../assets/account.png";
import TopBar from "../../../components/TopBar";
import ChangeUserRoleModal from "../ChangeUserRoleModal";
import InviteMemberModal from "../InviteMemberModal";
import {
  TravelAccountData,
  TravelAccountMember,
  TravelAccountMemberData,
} from "../type/type";
import Loading from "../component/Loading";
import client from "../../../client";
import { formatUserRole, getBeanImage } from "../util/util";

const MemberManagementPage: React.FC = () => {
  const { parentAccountId } = useParams();

  const [loading1, setLoading1] = useState(true);
  const [loading2, setLoading2] = useState(true);

  // 여행통장 상태관리
  const [travelAccountData, setTravelAccountData] =
    useState<TravelAccountData>();

  // 여행통장 멤버 상태관리
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

  // 멤버 역할 업데이트 함수
  const updateMemberRole = (updatedMember: TravelAccountMember) => {
    if (travelAccountMemberData) {
      const updatedMembers = travelAccountMemberData.members.map((member) =>
        member.userId === updatedMember.userId ? updatedMember : member,
      );
      setTravelAccountMemberData({
        ...travelAccountMemberData,
        members: updatedMembers,
      });
    }
  };

  const [isInviteModalOpen, setIsInviteModalOpen] = useState(false); // 초대 모달 상태

  const handleInviteMember = () => {
    setIsInviteModalOpen(true); // 초대 모달 열기
  };

  const closeInviteModal = () => {
    setIsInviteModalOpen(false); // 초대 모달 닫기
  };

  // Travel 서버 여행통장 조회 API fetch 요청
  useEffect(() => {
    const getTravelAccountData = async () => {
      const response = await client().get(`/api/travel/${parentAccountId}`);
      setTravelAccountData(response.data);
      setLoading1(false);
    };

    if (parentAccountId) {
      getTravelAccountData();
    }
  }, [parentAccountId]);

  // Account 서버 한화 여행통장 멤버 목록 조회 API fetch 요청
  useEffect(() => {
    const getTravelAccountData = async () => {
      const response = await client().get(
        `/api/accounts/travel/domestic/${parentAccountId}/members`,
      );
      setTravelAccountMemberData(response.data);
      setLoading2(false);
    };
    if (parentAccountId) {
      getTravelAccountData();
    }
  }, [parentAccountId]);

  // 로딩 중이면 로딩 스피너 표시
  if (loading1 || loading2) {
    return <Loading />;
  }

  return (
    <div
      className={`h-full relative ${isChangeUserRoleModalOpen ? "bg-gray-300" : ""}`}
    >
      {/* 네비게이션 바 */}
      <div className="pt-16">
        <TopBar page="멤버 관리" isWhite isLogo={false} />
      </div>

      {/* 통장 정보 */}
      <div className="px-4 py-2">
        <div className="flex items-center mb-4">
          <img src={account} alt="account" className="w-10 h-10 mr-2" />
          <div>
            <div className="text-lg">{travelAccountData?.accountName}</div>
            <div className="text-sm">
              {travelAccountData?.bankName} {travelAccountData?.accountNo}
            </div>
          </div>
        </div>
        <hr />
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
                <img
                  src={getBeanImage(member.role)}
                  alt={member.role}
                  className="w-10 h-10 mr-2"
                />
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

      {/* 초대하기 */}
      <div>
        <button
          type="button"
          onClick={handleInviteMember}
          className="btn-lg absolute bottom-10 left-0 right-0 w-[90%] mx-auto block"
        >
          초대하기
        </button>
      </div>

      {/* 유저 권한 변경 모달 */}
      {isChangeUserRoleModalOpen && memberInfo ? (
        <div className="absolute bottom-0 left-0 w-full">
          <ChangeUserRoleModal
            memberInfo={memberInfo}
            onClose={closeChangeUserRoleModal}
            onRoleChange={updateMemberRole}
          />
        </div>
      ) : null}

      {/* 초대하기 모달 */}
      {isInviteModalOpen ? (
        <div className="absolute inset-0 flex items-center justify-center bg-gray-900 bg-opacity-50">
          <InviteMemberModal
            onClose={closeInviteModal}
            accountId={parentAccountId}
          />
        </div>
      ) : null}
    </div>
  );
};

export default MemberManagementPage;
