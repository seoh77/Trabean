import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import CustomKeypad from "./Keypad";
import NavBar from "./NavBar";
import Modal from "./Modal";
import SuccessPage from "./SuccessPage";
import NextStepButton from "./NextStepButton";

const PasswordInputPage: React.FC = () => {
  const [step, setStep] = useState(1); // 현재 단계 (1: 통장 이름, 2: 목표 금액, 3: 비밀번호 입력, 4: 비밀번호 확인, 5: 성공 화면)
  const [accountName, setAccountName] = useState(""); // 통장 이름 상태
  const [targetAmount, setTargetAmount] = useState(""); // 목표 금액 상태
  const [password, setPassword] = useState(""); // 비밀번호 상태
  const [confirmPassword, setConfirmPassword] = useState(""); // 비밀번호 확인 상태
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const navigate = useNavigate(); // 화면 이동을 위한 네비게이션 hook

  useEffect(() => {
    const storedIsVerified = sessionStorage.getItem("isIdentityAuth");
    if (!storedIsVerified || storedIsVerified !== "true") {
      // 인증되지 않았으면 접근을 차단하고 리다이렉트
      navigate("/creation/travel");
    }
  }, [navigate]);

  // 통장 이름 입력 핸들러
  const handleAccountNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setAccountName(e.target.value);
  };

  // 목표 금액 입력 핸들러
  const handleTargetAmountChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setTargetAmount(e.target.value);
  };

  // 비밀번호 입력 변경 핸들러
  const handlePasswordChange = (newPassword: string) => {
    if (step === 3) {
      setPassword(newPassword);
    } else {
      setConfirmPassword(newPassword);
    }
  };

  // 완료 버튼 핸들러
  const handleComplete = () => {
    if (step === 3) {
      setStep(4); // 비밀번호 입력 완료 -> 확인 단계로 이동
    } else if (step === 4 && confirmPassword === password) {
      setStep(5); // 비밀번호가 일치하면 성공 페이지로 이동
    } else {
      setIsModalOpen(true);
      setStep(3);
      setPassword("");
      setConfirmPassword(""); // 비밀번호 확인 초기화
    }
  };

  // 다음 단계 이동
  const goToNextStep = () => {
    setStep(step + 1);
  };

  // 모달 닫기 핸들러
  const closeModal = () => {
    setIsModalOpen(false);
  };

  // 완료 버튼 핸들러
  const handleNextStep = () => {
    navigate(`/`);
  };

  return (
    <div className="px-6 py-8 bg-white">
      <NavBar text="여행 통장 개설" />

      {step === 1 && (
        <>
          <p className="text-md mb-8 text-center">
            멤버들과 함께 사용할{" "}
            <p>
              여행 계좌의 <span className="font-bold">별명</span>을 지어주세요.
            </p>
          </p>
          <input
            type="text"
            value={accountName}
            onChange={handleAccountNameChange}
            placeholder="최대 15글자까지 입력 가능합니다."
            className="border rounded-md p-2 mt-4 w-full mb-2"
          />
          <div className="flex justify-center px-6 mt-6">
            <NextStepButton
              isEnabled={accountName.length > 0}
              onClick={goToNextStep}
              text="다음"
            />
          </div>
        </>
      )}

      {step === 2 && (
        <>
          <h1 className="text-lg font-bold mb-1">목표 금액 설정</h1>
          <p className="text-md mb-8 text-gray-500">
            여행 계좌의 목표 금액을 설정해주세요.
          </p>
          <input
            type="text"
            value={targetAmount}
            onChange={handleTargetAmountChange}
            placeholder="0"
            className="border rounded-md p-2 mt-5 w-full mb-2 text-center"
          />
          <div className="flex justify-center px-6">
            <NextStepButton
              isEnabled={targetAmount.length > 0}
              onClick={goToNextStep}
              text="다음"
            />
          </div>
        </>
      )}

      {step === 3 && (
        <>
          <h1 className="text-lg font-bold mb-1">마지막 단계에요!</h1>
          <p className="text-md mb-8 text-gray-500">
            사용하실 통장 비밀번호를 입력해주세요
          </p>
          <p className="text-gray-500 mb-5 text-sm text-center">
            “모두가 함께 공유하는 비밀번호에요”
          </p>
          <CustomKeypad
            password={password}
            onChange={handlePasswordChange}
            onComplete={handleComplete}
          />
        </>
      )}

      {step === 4 && (
        <>
          <h1 className="text-lg font-bold mb-1">마지막 단계에요!</h1>
          <p className="text-md mb-8 text-gray-500">
            통장 비밀번호를 다시 한 번 입력해주세요
          </p>
          <p className="text-gray-500 mb-5 text-sm text-center">
            “비밀번호를 다시 확인하고 있어요”
          </p>
          <CustomKeypad
            password={confirmPassword}
            onChange={handlePasswordChange}
            onComplete={handleComplete}
          />
        </>
      )}

      {step === 5 && (
        <SuccessPage
          title="축하합니다!"
          message="여행 통장 개설에 성공하셨습니다."
        />
      )}

      {step === 5 && (
        <div className="flex justify-center px-6">
          <NextStepButton isEnabled onClick={handleNextStep} text="확인" />
        </div>
      )}

      {isModalOpen && (
        <Modal
          message="비밀번호가 일치하지 않습니다."
          subMessage={[
            {
              key: 1,
              text: "처음부터 다시 입력해주세요.",
              className: "text-xs text-gray-500",
            },
          ]}
          onClose={closeModal}
        />
      )}
    </div>
  );
};

export default PasswordInputPage;
