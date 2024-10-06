import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import CustomKeypad from "./Keypad"; // 비밀번호 입력 키패드 컴포넌트
import Modal from "./Modal"; // 모달 컴포넌트

const PasswordPage: React.FC = () => {
  const [password, setPassword] = useState(""); // 입력된 비밀번호 상태
  const [isModalOpen, setIsModalOpen] = useState(false); // 모달 상태 관리
  const [modalMessage, setModalMessage] = useState(""); // 모달 메시지
  const [subMessage, setSubMessage] = useState<string | null>(null); // 모달 서브 메시지
  const navigate = useNavigate(); // 페이지 이동을 위한 navigate 훅

  // 가상의 잔액 정보
  const accountBalance = 5000; // 예를 들어 잔액이 5000원이라고 가정

  // 비밀번호 변경 핸들러
  const handlePasswordChange = (newPassword: string) => {
    setPassword(newPassword);
  };

  // 완료 버튼 클릭 시 처리
  const handleComplete = () => {
    // 가상의 송금 금액 설정
    const transferAmount = 0; // 송금하려는 금액이 10000원이라고 가정

    if (accountBalance >= transferAmount) {
      // 잔액이 충분한 경우 성공 페이지로 이동
      navigate("/transfer/success"); // 송금 성공 페이지로 이동
    } else {
      // 잔액이 부족한 경우 모달 창 띄우기
      setModalMessage("이체 실패하였습니다");
      setSubMessage("사유: 잔액 부족");
      setIsModalOpen(true); // 모달을 열기
    }
  };

  // 모달 닫기
  const handleCloseModal = () => {
    setIsModalOpen(false);
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen p-4 bg-gray-50">
      <h1 className="text-xl font-bold mb-6">비밀번호 입력</h1>

      {/* CustomKeypad 컴포넌트 사용 */}
      <CustomKeypad
        password={password}
        onChange={handlePasswordChange}
        onComplete={handleComplete}
      />

      {/* 모달 */}
      {isModalOpen && (
        <Modal
          message={modalMessage}
          subMessage={[{ key: 1, text: subMessage || "" }]}
          onClose={handleCloseModal}
        />
      )}
    </div>
  );
};

export default PasswordPage;
