import React from "react";
import { useNavigate } from "react-router-dom";
import beanProfile from "../../assets/bean_on.png"; // 성공 이미지

const SuccessPage: React.FC = () => {
  const navigate = useNavigate();

  // 확인 버튼 클릭 시 홈으로 이동
  const handleConfirm = () => {
    navigate("/transfer/list"); // 홈 페이지나 원하는 경로로 이동
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen p-4 bg-gray-50">
      <img src={beanProfile} alt="성공" className="w-40 h-40 mb-6" />
      <h1 className="text-xl font-bold mb-4">송금이 완료되었습니다!</h1>
      <button
        type="button"
        onClick={handleConfirm}
        className="w-full max-w-xs bg-green-500 text-white py-3 rounded-lg"
      >
        확인
      </button>
    </div>
  );
};

export default SuccessPage;
