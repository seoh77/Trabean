import React, { useState, useEffect } from "react";
import { useParams, useLocation, useNavigate } from "react-router-dom";
import beanProfile from "../../assets/bean_profile.png"; // 기본 콩 이미지
import trabeanLogo from "../../assets/logo.png";
import soyIcon from "../../assets/icon/soyIcon.png";
import Keypad from "../AccountCreationPage/Keypad"; // Assuming you have this component
import Modal from "../AccountCreationPage/Modal"; // Assuming you have a reusable Modal component

interface TransferDetails {
  id?: number;
  name?: string;
  bank?: string;
  account: string;
}

const TransferList: React.FC = () => {
  const { account } = useParams<{ account: string }>(); // URL에서 계좌 번호 받기
  const location = useLocation();
  const navigate = useNavigate();
  const transferDetails = location.state as TransferDetails; // 이전 페이지에서 전달된 state 받기
  const [amount, setAmount] = useState<string>(""); // 송금 금액 관리
  const [isModalOpen, setIsModalOpen] = useState(false); // 모달 상태 관리
  const [password, setPassword] = useState<string>(""); // 비밀번호 관리
  const [confirmPassword, setConfirmPassword] = useState<string>(""); // 비밀번호 확인 단계
  const [attemptCount, setAttemptCount] = useState<number>(0); // 비밀번호 시도 횟수 관리
  const maxAttempts = 3; // 최대 시도 횟수
  const [modalMessage, setModalMessage] = useState<string>(""); // 모달 메시지
  const [subMessage, setSubMessage] = useState<
    { key: number; text: string; className: string }[]
  >([]); // 모달 서브 메시지
  const [isPasswordStep, setIsPasswordStep] = useState<boolean>(false); // 비밀번호 단계인지 여부
  const [isConfirmStep, setIsConfirmStep] = useState<boolean>(false); // 비밀번호 확인 단계인지 여부
  const [isSuccess, setIsSuccess] = useState<boolean | null>(null); // 송금 성공 여부

  // 비밀번호 입력 완료 핸들러
  const handlePasswordComplete = () => {
    if (!isConfirmStep) {
      setIsConfirmStep(true);
    } else if (password === confirmPassword) {
      // Simulate balance check (e.g., assume a balance of 50000원)
      const currentBalance = 50000;
      const transferAmount = parseInt(amount, 10);

      if (transferAmount <= currentBalance) {
        // Successful transfer
        setIsSuccess(true);
        sessionStorage.setItem("isVerified", "true");
        setModalMessage("송금이 완료되었습니다!");
      } else {
        // Failed due to insufficient funds
        setIsSuccess(false);
        setModalMessage("잔액이 부족합니다.");
      }
      setIsModalOpen(true);
    } else {
      setAttemptCount((prev) => prev + 1);
      if (attemptCount + 1 < maxAttempts) {
        setModalMessage("비밀번호가 일치하지 않습니다.");
        setSubMessage([
          {
            key: 1,
            text: "다시 입력해주세요.",
            className: "text-xs text-gray-500",
          },
          {
            key: 2,
            text: `남은 시도 횟수 (${attemptCount + 1}/${maxAttempts})`,
            className: "text-xs text-red-500",
          },
        ]);
      }
      setIsModalOpen(true);
      setPassword("");
      setConfirmPassword("");
      setIsConfirmStep(false);
    }
  };

  // 번호 입력시 password 갱신
  const handlePasswordChange = (newPassword: string) => {
    if (!isConfirmStep) {
      setPassword(newPassword);
    } else {
      setConfirmPassword(newPassword);
    }
  };

  // 시도 횟수 초과 시 처리
  useEffect(() => {
    if (attemptCount >= maxAttempts) {
      setModalMessage("입력 가능 횟수를 초과하였습니다.");
      setSubMessage([
        {
          key: 1,
          text: "잠시 후 다시 시도해주세요.",
          className: "text-xs text-red-500",
        },
      ]);
      setIsModalOpen(true);

      setTimeout(() => {
        navigate("/accounts/travel/domestic");
      }, 3000);
    }
  }, [attemptCount, maxAttempts, navigate]);

  // 송금 버튼 클릭 처리
  const handleSend = () => {
    setIsPasswordStep(true); // 비밀번호 단계로 이동
  };

  // 모달 닫기 핸들러
  const closeModal = () => {
    setIsModalOpen(false);
    if (isSuccess) {
      navigate("/transfer/success"); // Redirect to a success page if needed
    }
  };

  return (
    <div className="relative p-4 flex flex-col items-center justify-center min-h-screen bg-gray-50">
      <img src={trabeanLogo} alt="로고" className="h-5 mb-3" />

      {!isPasswordStep && (
        <>
          {/* 프로필 및 계좌 정보 */}
          <div className="flex items-center mb-6">
            <div className="w-12 h-12 rounded-full bg-green-100 flex items-center justify-center">
              <img
                src={password ? beanProfile : beanProfile}
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
            <span className="text-2xl font-bold">{amount || "0"} 원</span>
            <button
              type="button"
              onClick={() => setAmount("")}
              className="ml-2"
            >
              <img src={soyIcon} alt="clear" className="w-6 h-6" />
            </button>
          </div>

          <div className="border-b-2 border-gray-300 w-full mb-6" />

          <div className="w-full max-w-md">
            <div className="grid grid-cols-3 gap-4">
              {[
                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8",
                "9",
                "00",
                "0",
                "⌫",
              ].map((number) => (
                <button
                  key={number}
                  type="button"
                  className="py-4 text-xl font-semibold bg-gray-200 rounded-lg"
                  onClick={() =>
                    number === "⌫"
                      ? setAmount(amount.slice(0, -1))
                      : setAmount((prev) => prev + number)
                  }
                >
                  {number}
                </button>
              ))}
            </div>
          </div>

          <button
            type="button"
            className="w-full max-w-md bg-green-500 text-white py-3 mt-4 rounded-lg text-lg font-semibold"
            onClick={handleSend}
          >
            송금
          </button>
        </>
      )}

      {isPasswordStep && (
        <div className="w-full mx-auto px-6 py-8 min-h-screen mt-16">
          <p className="text-md mb-4 text-black text-center font-semibold">
            {isConfirmStep
              ? "비밀번호를 다시 입력해주세요"
              : "비밀번호를 입력해주세요"}
          </p>
          <p className="text-gray-700 mb-6 text-sm text-center">
            “6자리 비밀번호를 {isConfirmStep ? "다시" : ""} 입력해주세요”
          </p>
          <Keypad
            password={isConfirmStep ? confirmPassword : password}
            onChange={handlePasswordChange}
            onComplete={handlePasswordComplete}
          />
        </div>
      )}

      {/* 모달 */}
      {isModalOpen && (
        <Modal
          message={modalMessage}
          subMessage={subMessage}
          onClose={closeModal}
        />
      )}
    </div>
  );
};

export default TransferList;
