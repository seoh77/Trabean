import React from "react";

interface ChangeTargetAmountModalProps {
  onClose: () => void;
}

const ChangeTargetAmountModal: React.FC<ChangeTargetAmountModalProps> = ({
  onClose,
}) => {
  const hanbleCloseModal = () => {
    onClose();
  };

  const handleUpdateTargetAmount = () => {
    console.log("확인 누름");
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
          type="text"
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
