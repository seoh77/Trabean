import React, { useState } from "react";
import { useParams, useLocation, useNavigate } from "react-router-dom"; // useNavigate 추가
import beanProfile from "../../assets/bean_profile.png";
import trabeanLogo from "../../assets/logo.png";
import { formatNumberWithCommas } from "../../utils/formatNumber";

interface TransferDetails {
  id?: number;
  name?: string;
  bank?: string;
  account: string;
}

const TransferList: React.FC = () => {
  const { account } = useParams<{ account: string }>(); // URL에서 계좌 번호 받기
  const location = useLocation();
  const navigate = useNavigate(); // useNavigate 훅
  const transferDetails = location.state as TransferDetails; // 이전 페이지에서 전달된 state 받기
  const [amount, setAmount] = useState<string>(""); // 송금 금액 관리
  const [isModalOpen, setIsModalOpen] = useState(false); // 모달 상태 관리

  // 숫자 클릭 처리
  const handleNumberClick = (value: string) => {
    setAmount((prev) => prev + value);
  };

  // 금액 지우기 처리
  const handleClear = () => {
    setAmount("");
  };

  // 모달 열기
  const handleSend = () => {
    setIsModalOpen(true);
  };

  // 모달 닫기
  const handleCloseModal = () => {
    setIsModalOpen(false);
  };

  // 비밀번호 입력 페이지로 이동
  const handleConfirm = () => {
    navigate("/transfer/password"); // 비밀번호 입력 페이지로 이동
  };

  return (
    <div className="relative p-4 flex flex-col items-center justify-center min-h-screen bg-gray-50">
      <img
        src={trabeanLogo}
        alt="로고"
        className="max-w-xs max-h-xs object-cover"
      />

      {/* 프로필 및 계좌 정보 */}
      <div className="flex items-center mb-6">
        <div className="w-12 h-12 rounded-full bg-green-100 flex items-center justify-center">
          <img
            src={beanProfile}
            alt="profile"
            className="w-full h-full object-cover rounded-full"
          />
        </div>
        <div className="ml-4">
          <p className="text-lg font-semibold">
            {transferDetails?.name || "직접 입력된 계좌"}
          </p>
          <p className="text-sm text-gray-600">
            {transferDetails?.bank || "정보 없음"}
          </p>
          <p className="text-sm text-gray-600">계좌 번호: {account}</p>
        </div>
      </div>

      {/* 금액 입력 */}
      <div className="w-full flex justify-center items-center mb-6">
        <span className="text-2xl font-bold">
          {formatNumberWithCommas(parseInt(amount, 10)) || "0"} 원
        </span>
        <button type="button" onClick={handleClear} className="ml-2">
          <img src="/assets/clear-icon.png" alt="clear" className="w-6 h-6" />
        </button>
      </div>

      {/* 입력 밑줄 */}
      <div className="border-b-2 border-gray-300 w-full mb-6"> </div>

      {/* 숫자 패드 */}
      <div className="w-full max-w-md">
        <div className="grid grid-cols-3 gap-4">
          {["1", "2", "3", "4", "5", "6", "7", "8", "9", "00", "0", "⌫"].map(
            (number) => (
              <button
                key={number}
                type="button"
                className="py-4 text-xl font-semibold bg-gray-200 rounded-lg"
                onClick={() =>
                  number === "⌫"
                    ? setAmount(amount.slice(0, -1))
                    : handleNumberClick(number)
                }
              >
                {number}
              </button>
            ),
          )}
        </div>
      </div>

      {/* 송금 버튼 */}
      <button
        type="button"
        className="w-full max-w-md bg-green-500 text-white py-3 mt-4 rounded-lg text-lg font-semibold"
        onClick={handleSend}
      >
        송금
      </button>

      {/* 모달 */}
      {isModalOpen && (
        <div className="absolute inset-0 flex items-center justify-center bg-black bg-opacity-50">
          <div className="bg-white p-6 rounded-lg shadow-lg w-full max-w-xs">
            {/* 모달의 너비를 적절히 조정 */}
            <div className="mb-4 text-center">
              <div className="font-semibold text-lg">
                {transferDetails?.name}
                님께
              </div>
              <div>송금하시겠습니까?</div>
            </div>
            <div className="flex justify-around space-x-4">
              {/* 버튼 간의 간격 추가 */}
              <button
                type="button"
                className="bg-gray-200 py-2 px-4 rounded-lg"
                onClick={handleCloseModal}
              >
                취소
              </button>
              <button
                type="button"
                className="bg-green-500 text-white py-2 px-4 rounded-lg"
                onClick={handleConfirm} // 확인 버튼 클릭 시 비밀번호 입력 페이지로 이동
              >
                확인
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default TransferList;
