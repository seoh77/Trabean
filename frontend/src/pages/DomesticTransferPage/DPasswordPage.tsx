import React, { useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import CustomKeypad from "../AccountCreationPage/Keypad"; // 비밀번호 입력 키패드 컴포넌트
import Modal from "../AccountCreationPage/Modal"; // 모달 컴포넌트
import client from "../../client";
import trabeanLogo from "../../assets/logo.png";
import TopBar from "../../components/TopBar";

const PasswordPage: React.FC = () => {
  const location = useLocation();
  const { accountId } = useParams<{ accountId: string }>();
  const { amount } = location.state || { amount: 0 };
  const { depositAccountNo } = location.state || { depositAccountNo: 0 };
  const { withdrawalAccountNo } = location.state || { withdrawalAccountNo: 0 };
  const [password, setPassword] = useState(""); // 입력된 비밀번호 상태
  const [isModalOpen, setIsModalOpen] = useState(false); // 모달 상태 관리
  const [modalMessage, setModalMessage] = useState(""); // 모달 메시지
  const [subMessage, setSubMessage] = useState<string | null>(null); // 모달 서브 메시지
  const navigate = useNavigate(); // 페이지 이동을 위한 navigate 훅

  // 비밀번호 변경 핸들러
  const handlePasswordChange = (newPassword: string) => {
    setPassword(newPassword);
  };

  // 완료 버튼 클릭 시 처리
  const handleComplete = async () => {
    // 비밀번호 검증 API 요청
    await client()
      .post(`/api/accounts/travel/domestic/${accountId}/verify`, {
        password,
      })
      .then((response) => {
        if (response.status === 200) {
          // 비밀번호 검증 성공 시 송금 요청
          client()
            .post(`/api/accounts/travel/domestic/${accountId}/transfer`, {
              depositAccountNo,
              withdrawalAccountNo,
              transactionBalance: amount, // 송금 금액을 전송
            })
            .then((transferResponse) => {
              if (transferResponse.status === 200) {
                // 송금 성공 시 성공 페이지로 이동
                navigate("/transfer/success/domestic");
              } else {
                // 송금 실패 시 모달 메시지 출력
                setModalMessage("이체 실패하였습니다.");
                setSubMessage("사유: 잔액 부족 또는 기타 문제");
                setIsModalOpen(true);
              }
            })
            // eslint-disable-next-line @typescript-eslint/no-unused-vars
            .catch((error) => {
              // 송금 요청 중 오류 발생 시 처리
              setModalMessage("송금 중 오류가 발생했습니다.");
              setSubMessage("사유: 서버 문제 또는 네트워크 오류");
              setIsModalOpen(true);
            });
        }
      })
      .catch((error) => {
        // 비밀번호 검증 실패 또는 기타 오류 처리
        if (error.response && error.response.status === 403) {
          setModalMessage("비밀번호가 틀렸습니다.");
          setSubMessage("사유: 잘못된 비밀번호");
        } else if (error.response && error.response.status === 500) {
          setModalMessage("서버 오류가 발생했습니다.");
          setSubMessage("사유: 서버 문제");
        } else {
          setModalMessage("알 수 없는 오류가 발생했습니다.");
          setSubMessage(null);
        }
        setIsModalOpen(true);
      });
    setPassword("");
  };

  // 모달 닫기
  const handleCloseModal = () => {
    setIsModalOpen(false);
  };

  return (
    <div>
      <TopBar isLogo={trabeanLogo} page="비밀번호 입력" isWhite />
      <div className="px-6 py-20 bg-white">
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
    </div>
  );
};

export default PasswordPage;
