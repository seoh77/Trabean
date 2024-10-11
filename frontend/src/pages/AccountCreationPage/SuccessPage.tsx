import React from "react";

// SuccessPage 컴포넌트의 props 타입 정의
interface SuccessPageProps {
  title: string; // 제목
  message: string; // 설명 메시지
}

const SuccessPage: React.FC<SuccessPageProps> = ({ title, message }) => (
  <div className="text-center p-4">
    <div className="flex justify-center items-center mb-4">
      <div className="flex justify-center">
        <div className="check-icon mt-8 mb-6 bg-primary"> </div>
      </div>
    </div>
    <h1 className="text-xl font-bold mb-4">{title}</h1>
    <p className="text-gray-500 mb-4">{message}</p>

    {/* 스타일링 */}
    <style>{`
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
          width: 30px;
          height: 60px;
          border: solid white;
          border-width: 0 10px 10px 0;
          transform: rotate(45deg);
          position: absolute;
          top: 50%; 
          left: 50%;
          transform: translate(-50%, -60%) rotate(45deg); 
        }
      `}</style>
  </div>
);

export default SuccessPage;
