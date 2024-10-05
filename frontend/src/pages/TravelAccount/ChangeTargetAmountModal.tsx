import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import client from "../../client";

interface ChangeTargetAmountModalProps {
  accountId: string | undefined;
  onClose: () => void;
  onTargetAmountChange: (newAmount: number) => void;
}

const ChangeTargetAmountModal: React.FC<ChangeTargetAmountModalProps> = ({
  accountId,
  onClose,
  onTargetAmountChange,
}) => {
  const [targetAmount, setTargetAmount] = useState("");

  const nav = useNavigate();

  const hanbleCloseModal = () => {
    onClose();
  };

  const handleUpdateTargetAmount = () => {
    if (targetAmount === "" || Number.isNaN(Number(targetAmount))) {
      alert("유효한 금액을 입력하세요.");
      return;
    }

    const fetchChangeTargetAmount = async () => {
      await client().put("/api/travel/change/targetAmount", {
        accountId,
        targetAmount,
      });
      onTargetAmountChange(Number(targetAmount));
      nav(`/accounts/travel/domestic/${accountId}`);
    };

    fetchChangeTargetAmount();

    onClose();
  };

  return (
    <div className="rounded-t-3xl p-4 bg-white">
      <div className="p-4">
        <div className="font-bold">총 얼마를 모을까요?</div>
        <div className="mt-4">금액</div>
      </div>
      <div className="flex p-4">
        <input
          type="number"
          value={targetAmount}
          onChange={({ target: { value } }) => setTargetAmount(value)} // 구조 분해 할당 사용
          min="0"
          className="flex-grow border-b-2 border-gray-200 outline-none"
        />
        <div className="ml-2">원</div>
      </div>
      <div className="flex justify-between px-4 py-8">
        <button
          type="button"
          onClick={hanbleCloseModal}
          className="btn-light-lg w-1/2 mr-2 text-center"
        >
          취소
        </button>
        <button
          type="button"
          onClick={handleUpdateTargetAmount}
          className="btn-lg w-1/2 ml-2 text-center"
        >
          확인
        </button>
      </div>
    </div>
  );
};

export default ChangeTargetAmountModal;
