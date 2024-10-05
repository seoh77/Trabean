import React, { useState } from "react";
import client from "../../../client";

interface ChangeTargetAmountModalProps {
  accountId: string | undefined;
  onTargetAmountChange: (newAmount: number) => void;
  onClose: () => void;
}

const ChangeTargetAmountModal: React.FC<ChangeTargetAmountModalProps> = ({
  accountId,
  onTargetAmountChange,
  onClose,
}) => {
  const [targetAmount, setTargetAmount] = useState(""); // 변경할 금액 상태관리

  // 확인 버튼을 눌렀을 때 서버로 PUT 요청 전송
  const handleUpdateTargetAmount = () => {
    if (targetAmount === "" || Number.isNaN(Number(targetAmount))) {
      alert("유효한 금액을 입력하세요.");
      return;
    }

    const fetchUpdateTargetAmount = async () => {
      try {
        await client().put("/api/travel/change/targetAmount", {
          accountId,
          targetAmount,
        });
        alert("목표 금액이 성공적으로 변경되었습니다!");
        onTargetAmountChange(Number(targetAmount));
        onClose();
      } catch (error) {
        alert("목표 금액 변경에 실패했습니다.");
        console.error(error);
      }
    };

    fetchUpdateTargetAmount();
  };

  return (
    <div className="bg-white p-4 rounded-t-3xl">
      <div className="p-4">
        <div className="font-bold mb-4">총 얼마를 모을까요?</div>
        <div>금액</div>
      </div>
      <div className="flex p-4">
        <input
          type="text"
          value={targetAmount}
          onChange={({ target: { value } }) => setTargetAmount(value)}
          min="0"
          className="flex-grow border-b-2 border-gray-300 outline-none mr-2"
        />
        <div>원</div>
      </div>
      <div className="flex justify-between p-4 mb-4">
        <button
          type="button"
          onClick={onClose}
          className="btn-light-lg w-1/2 mr-2"
        >
          취소
        </button>
        <button
          type="button"
          onClick={handleUpdateTargetAmount}
          className="btn-lg w-1/2 ml-2"
        >
          확인
        </button>
      </div>
    </div>
  );
};

export default ChangeTargetAmountModal;
