import React, { useState } from "react";
import client from "../../client";

interface InviteMemberModalProps {
  onClose: () => void;
  accountId: string | undefined; // 여행 통장의 ID
}

const InviteMemberModal: React.FC<InviteMemberModalProps> = ({
  onClose,
  accountId,
}) => {
  const [email, setEmail] = useState("");

  const handleEmailChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setEmail(e.target.value);
  };

  const handleInviteSubmit = async () => {
    if (!email) {
      alert("이메일을 입력해주세요.");
      return;
    }

    try {
      await client().post("/api/travel/invitation", {
        accountId,
        email,
      });
      alert("초대가 완료되었습니다.");
      onClose(); // 모달 닫기
    } catch (error) {
      alert("초대 요청에 실패했습니다.");
    }
  };

  return (
    <div className="bg-white p-6 rounded-lg shadow-lg w-80">
      <h2 className="text-xl mb-4">멤버 초대</h2>
      <input
        type="email"
        value={email}
        onChange={handleEmailChange}
        placeholder="이메일 입력"
        className="border p-2 w-full mb-4"
      />
      <div className="flex justify-between">
        <button
          type="button"
          onClick={onClose}
          className="btn-light-lg w-1/2 mr-2 text-center"
        >
          취소
        </button>
        <button
          type="button"
          onClick={handleInviteSubmit}
          className="btn-lg w-1/2 ml-2 text-center"
        >
          확인
        </button>
      </div>
    </div>
  );
};

export default InviteMemberModal;
