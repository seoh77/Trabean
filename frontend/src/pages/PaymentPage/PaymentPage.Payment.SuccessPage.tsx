import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

import TopBar from "../../components/TopBar";
import SuccessIcon from "../../assets/successIcon.png";

const SuccessPage: React.FC = () => {
  const [isLoading, setIsLoading] = useState<boolean>(true);

  const navigate = useNavigate();
  const goHome = () => {
    navigate("/");
  };

  useEffect(() => {
    setTimeout(() => {
      setIsLoading(false);
    }, 1500);
  }, []);

  return (
    <div>
      <TopBar isWhite isLogo={false} page="QR 결제" />
      <div className="flex flex-col items-center justify-center h-dvh">
        {isLoading && (
          <div className="flex flex-col items-center">
            <div className="circle-loader" />
            <p className="mt-8">결제 진행 중입니다</p>
          </div>
        )}
        {!isLoading && (
          <>
            <img
              src={SuccessIcon}
              alt="success-icon"
              className="w-[70px] h-[70px]"
            />
            <p className="text-2xl my-[22px] text-gray-700 font-semibold">
              축하합니다 !
            </p>
            <p className="text-lg text-gray-700">결제에 성공하셨습니다.</p>
            <button
              type="button"
              className="btn-lg mt-[20px] w-[238px]"
              onClick={goHome}
            >
              확인
            </button>
          </>
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
    </div>
  );
};

export default SuccessPage;
