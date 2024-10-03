import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import NavBar from "./NavBar";
import NextStepButton from "./NextStepButton";
import Modal from "./Modal";

interface SubMessage {
  key: string | number;
  text: string;
  className?: string;
}

const AccountVerificationPage: React.FC = () => {
  const query = new URLSearchParams(useLocation().search);
  const bankName = query.get("bank") || "은행";
  const [accountNumber, setAccountNumber] = useState<string>("");
  const [verificationCode, setVerificationCode] = useState<string>("");
  const [step, setStep] = useState<number>(1); // 현재 단계 관리
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [modalMessage, setModalMessage] = useState<string>("");
  const [subMessage, setSubMessage] = useState<SubMessage[]>([]); // 배열 형태로 수정
  const [attemptCount, setAttemptCount] = useState(0); // 현재 시도 횟수 상태
  const maxAttempts = 5; // 최대 시도 횟수
  const navigate = useNavigate(); // 화면 이동을 위한 네비게이션 hook

  useEffect(() => {
    const verifyAccount = async () => {
      if (step === 2) {
        try {
          // const response = await fetch(
          //   "https://j11a604.p.ssafy.io/api/accounts/verification/account",
          //   {
          //     method: "POST",
          //     headers: {
          //       "Content-Type": "application/json",
          //       userId: "12345", // 유저 식별자
          //       userKey: "sampleUserKey", // 유저 Key
          //     },
          //     body: JSON.stringify({
          //       accountNo: accountNumber,
          //     }),
          //   },
          // );

          // if (response.ok) {
          //   setStep(3);
          //   setTimeout(() => {
          //     setStep(4);
          //   }, 6000); // 1초 후 인증번호 입력 화면으로 이동
          // } else {
          //   setModalMessage("계좌 인증에 실패했습니다.");
          //   setSubMessage("다시 시도해주세요.");
          //   setIsModalOpen(true);
          // }

          const response = { ok: true };

          setTimeout(() => {
            if (response.ok) {
              setStep(3);
              setTimeout(() => {
                setStep(4);
              }, 1000); // 1초 후 인증번호 입력 화면으로 이동
            } else {
              setModalMessage("계좌 인증에 실패했습니다.");
              setSubMessage([
                {
                  key: 1,
                  text: "잠시 후 다시 시도해주세요.",
                  className: "text-xs text-gray-500",
                },
              ]);
              setIsModalOpen(true);
            }
          }, 2000);
        } catch (error) {
          setModalMessage("서버에 문제가 발생했습니다.");
          setSubMessage([
            {
              key: 1,
              text: "잠시 후 다시 시도해주세요.",
              className: "text-xs text-gray-500",
            },
          ]);
          setIsModalOpen(true);
        }
      }
    };

    verifyAccount();
  }, [step, accountNumber]);

  useEffect(() => {
    if (attemptCount >= maxAttempts) {
      setModalMessage("인증 가능 횟수를 초과하였습니다.");
      setSubMessage([
        {
          key: 1,
          text: "잠시 후 다시 시도해주세요.",
          className: "text-xs text-red-500",
        },
      ]);
      setIsModalOpen(true);

      // 3초 후에 다른 페이지로 이동
      setTimeout(() => {
        navigate("/"); // 이동할 페이지 경로 설정
      }, 3000);
    }
  }, [attemptCount, maxAttempts, navigate]);

  useEffect(() => {
    const storedIsVerified = sessionStorage.getItem("isSelectedBank"); // sessionStorage로 변경
    if (!storedIsVerified || storedIsVerified !== "true") {
      // 은행이 선택되지 않았으면 접근을 차단하고 리다이렉트
      navigate("/creation/travel");
    }
  }, [navigate]);

  // 계좌번호 입력 핸들러
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setAccountNumber(e.target.value);
  };

  // 인증번호 입력 핸들러
  const handleCodeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setVerificationCode(e.target.value);
  };

  // 계좌번호 유효성 검사
  const validateAccountNumber = (number: string) => {
    const regex = /^\d{8,10}$/; // 8자리 또는 10자리 숫자
    return regex.test(number);
  };

  const isVerificationCodeValid = async () => {
    // API 요청에 사용할 header 및 body 데이터를 설정합니다.
    const headers = {
      userId: "12345", // 유저 식별자 (실제 값을 대체)
      userKey: "sampleUserKey", // 유저 Key (실제 값을 대체)
    };

    const body = JSON.stringify({
      accountNo: accountNumber, // 사용자가 입력한 계좌번호
      otp: verificationCode, // 사용자가 입력한 4자리 인증번호
    });

    try {
      const response = await fetch(
        "https://j11a604.p.ssafy.io/api/accounts/verification/otp", // API 엔드포인트 URL
        {
          method: "POST",
          headers,
          body,
        },
      );

      return response.ok; // 응답이 성공적인 경우 true 반환
    } catch (error) {
      console.error("인증번호 검증 요청 실패:", error);
      // return false; // 요청 실패 시 false 반환
      return true;
    }
  };

  // 단계별 다음 버튼 핸들러
  const handleNextStep = async () => {
    switch (step) {
      case 1:
        if (!validateAccountNumber(accountNumber)) {
          setModalMessage("유효하지 않은 계좌번호입니다.");
          setSubMessage([
            {
              key: 1,
              text: "계좌번호를 다시 확인해주세요.",
              className: "text-xs text-gray-500",
            },
          ]);
          setIsModalOpen(true);
        } else {
          setStep(2);
        }
        break;
      case 2:
        setStep(3);
        break;
      case 3:
        setStep(4); // 인증 완료 후 인증번호 입력 화면으로 이동
        break;
      case 4:
        if (verificationCode.length === 4) {
          const isValid = await isVerificationCodeValid();
          if (isValid) {
            sessionStorage.setItem("isVerified", "true");
            navigate("/creation/travel/identity-auth");
          } else {
            // 시도 횟수 증가 및 상태 업데이트
            setAttemptCount((prev) => prev + 1);
            if (attemptCount + 1 < maxAttempts) {
              setModalMessage("올바르지 않은 인증번호입니다.");
              setSubMessage([
                {
                  key: 1,
                  text: "입금 내역을 다시 확인해주세요.",
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
          }
        }
        break;
      default:
        break;
    }
  };

  // 모달 닫기 핸들러
  const closeModal = () => {
    setIsModalOpen(false);
  };

  // 단계별 화면 컴포넌트
  const renderStepContent = () => {
    switch (step) {
      case 1:
        return (
          <div className="mb-10">
            <h1 className="text-xl font-bold mt-10">{bankName}은행</h1>
            <p>계좌번호를 입력하세요</p>
            <input
              type="text"
              value={accountNumber}
              onChange={handleInputChange}
              placeholder="계좌번호"
              className="border rounded-md p-2 mt-5 w-full mb-10"
            />
          </div>
        );
      case 2:
        return (
          <div className="text-left mt-10">
            <h1 className="text-lg font-bold mb-4">
              계좌 인증을 위해 <br />
              1원을 보내볼게요
            </h1>
            <p className="mb-4">
              <span className="font-bold">{bankName}은행</span> {accountNumber}
            </p>
            <div className="my-20">
              <div className="circle-loader my-10"> </div>
            </div>
          </div>
        );
      case 3:
        return (
          <div className="text-left mt-10">
            <h1 className="text-lg font-bold mb-4">
              계좌 인증을 위해 <br />
              1원을 보내볼게요
            </h1>
            <p className="mb-4">
              <span className="font-bold">{bankName}은행</span> {accountNumber}
            </p>
            <div className="flex justify-center">
              <div className="check-icon my-14 bg-primary"> </div>
            </div>
          </div>
        );
      case 4:
        return (
          <div className="text-left mt-10">
            <h1 className="text-lg font-bold mb-4">
              우리은행 입금내역을 확인하고 <br />
              ‘SSAFY’ 뒤 4자리 숫자를 입력해주세요
            </h1>
            <p className="mb-4">
              {bankName} {accountNumber}
            </p>
            <input
              type="text"
              value={verificationCode}
              onChange={handleCodeChange}
              placeholder="1234"
              className="border rounded-md p-2 w-full mb-10"
            />
          </div>
        );
      default:
        return null;
    }
  };

  return (
    <div className="px-6 py-8 bg-white">
      <NavBar text="여행 통장 개설" />
      {renderStepContent()}

      <div className="flex justify-center mt-10">
        {(step === 1 || step === 4) && (
          <NextStepButton
            isEnabled={
              (step === 1 && accountNumber.length > 0) ||
              (step === 4 && verificationCode.length === 4)
            }
            onClick={handleNextStep}
            text="다음 단계"
          />
        )}
      </div>

      {isModalOpen && (
        <Modal
          message={modalMessage}
          subMessage={subMessage}
          onClose={closeModal}
        />
      )}

      {/* 로딩 애니메이션 및 체크 아이콘을 위한 스타일 */}
      <style>{`
      .circle-loader {
          border: 20px solid #e5e7eb;
          border-radius: 50%;
          border-top: 20px solid #4caf50;
          width: 150px;
          height: 150px;
          animation: spin 1s linear infinite;
          margin: 0 auto;
        }
        @keyframes spin {
          0% { transform: rotate(0deg); }
          100% { transform: rotate(360deg); }
        }
        .check-icon {
          width: 150px;
          height: 150px;
          border-radius: 50%;
          display: flex;
          position: relative;
        }
        .check-icon::after {
          content: '';
          position: absolute;
          width: 40px;
          height: 80px;
          border: solid white;
          border-width: 0 13px 13px 0;
          transform: rotate(45deg);
          position: absolute;
          top: 50%; 
          left: 50%;
          transform: translate(-50%, -60%) rotate(45deg); 
        }
      `}</style>
    </div>
  );
};

export default AccountVerificationPage;
