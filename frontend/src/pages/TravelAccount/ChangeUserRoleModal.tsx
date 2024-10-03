import React from "react";

import bean from "../../assets/bean.png";

interface Member {
  userId: number;
  userName: string;
  role: string;
}

interface ChangeUserRoleModalProps {
  memberInfo: Member;
  onClose: () => void;
}

const ChangeUserRoleModal: React.FC<ChangeUserRoleModalProps> = ({
  memberInfo,
  onClose,
}) => {
  const hanbleCloseModal = () => {
    onClose();
  };

  const handleUpdateUserRole = () => {
    console.log("설정 완료 누름");
    onClose();
  };

  // 역할에 따라 메시지 결정
  const getRoleMessage = (role: string) => {
    switch (role) {
      case "PAYER":
        return "결제 불가";
      case "NONE_PAYER":
        return "결제 가능";
      default:
        return "";
    }
  };

  // 역할에 따라 글씨 색상 결정
  const getRoleTextColor = (role: string) =>
    role === "PAYER" ? "text-red-500" : "text-green-500";

  return (
    <div className="rounded-t-3xl p-4 bg-white">
      <div className="flex flex-col items-center">
        <div className="p-4">
          <img src={bean} alt="bean" className="w-10 h-10" />
          <div>{memberInfo.userName}</div>
        </div>
        <div>{memberInfo.userName}님의 결제 권한을</div>
        <div>
          <span className={getRoleTextColor(memberInfo.role)}>
            {getRoleMessage(memberInfo.role)}
          </span>
          (으)로 설정하시겠습니까?
        </div>
      </div>
      <div className="flex justify-between px-4 py-8">
        <button
          type="button"
          onClick={hanbleCloseModal}
          className="btn-light-lg w-1/2 mr-2 text-center"
        >
          돌아 가기
        </button>
        <button
          type="button"
          onClick={handleUpdateUserRole}
          className="btn-lg w-1/2 ml-2 text-center"
        >
          설정 완료
        </button>
      </div>
    </div>
  );
};

export default ChangeUserRoleModal;
