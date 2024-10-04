import React from "react";
import { useNavigate } from "react-router-dom";
import NavBar from "./NavBar";
import NextStepButton from "./NextStepButton";
import beans from "../../assets/beans.png";
import logo from "../../assets/logo.png";

const CreationMainPage: React.FC = () => {
  const navigate = useNavigate(); // 페이지 이동을 위한 navigate 훅

  return (
    <div className="px-6 py-8 bg-white">
      <NavBar text="여행 통장 개설" />

      <div className="flex flex-col items-center justify-center bg-white px-6 mt-10">
        <p className="text-center text-gray-500 mb-2">트래빈뱅크 여행통장</p>
        <h2 className="text-center text-2xl font-semibold mb-10">
          함께 하는 여행, <br />
          함께 모아보세요
        </h2>

        {/* 이미지 영역 */}
        <div className="mb-2">
          <img src={beans} alt="Trabean Logo" className="w-68 h-auto" />
        </div>

        {/* 브랜드 로고 */}
        <div className="mb-10">
          <img src={logo} alt="Trabean Text Logo" className="w-24 h-auto" />
        </div>

        {/* 시작하기 버튼 */}
        <div className="flex justify-center mt-10 w-full">
          <NextStepButton
            isEnabled
            onClick={() => navigate("/creation/travel/bank")}
            text="시작하기"
          />
        </div>
      </div>
    </div>
  );
};

export default CreationMainPage;
