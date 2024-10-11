import React from "react";
import impotantIcon from "../../assets/importantIcon.png";

interface FailModalProps {
  message: string | null;
  handleModal: () => void;
  isRetry: boolean;
  retryPaymentRequest: () => void;
}

const FailModal: React.FC<FailModalProps> = ({
  message,
  handleModal,
  isRetry,
  retryPaymentRequest,
}) => {
  console.log("dd");
  return (
    <div className="fixed z-[200] w-[360px] h-dvh bg-black/70 top-0 flex flex-col items-center justify-center">
      <div className="w-[320px] bg-white flex flex-col items-center rounded-[20px] py-[20px] relative">
        <span
          onClick={handleModal}
          role="presentation"
          className="hover:cursor-pointer text-xl absolute right-5 font-semibold top-2"
        >
          X
        </span>
        <img
          src={impotantIcon}
          alt="impotant-icon"
          className="mt-[35px] w-[32px] h-[32px]"
        />
        <p className="text-xl mt-[20px]">결제에 실패하였습니다.</p>
        <p className="text-red-600 mt-[5px] px-4 text-center">{message}</p>
        {isRetry && (
          <button
            type="button"
            className="btn-lg mt-[20px] w-[80%]"
            onClick={retryPaymentRequest}
          >
            {" "}
            한화로 결제
          </button>
        )}
      </div>
    </div>
  );
};

export default FailModal;
