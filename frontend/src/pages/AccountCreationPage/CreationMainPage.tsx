import React from "react";
import { useNavigate } from "react-router-dom";
import { useAccountType } from "./AccountTypeContext";
import NextStepButton from "./NextStepButton";
import CreationMain from "./CreationMain";
import beans from "../../assets/beans.png";
import bean from "../../assets/bean_personal.png";
import logo from "../../assets/logo.png";

const CreationMainPage: React.FC = () => {
  const { accountType, resetAccountType } = useAccountType(); // Context 값 가져오기
  const navigate = useNavigate();

  const returnPage = () => {
    resetAccountType(); // Context 상태 초기화
    navigate(-1); // 이전 페이지로 이동
  };

  if (!accountType) {
    return (
      <div className="flex justify-center items-center h-screen">
        <div className="text-left">
          <div className="font-bold text-lg">! 잘못된 접근 입니다.</div>
          <div className="mb-10">통장 유형이 선택되지 않았습니다.</div>
          <div className="flex justify-center">
            <NextStepButton isEnabled onClick={returnPage} text="돌아가기" />
          </div>
        </div>
      </div>
    );
  }

  if (accountType === "personal") {
    return (
      <CreationMain
        navText="개인 통장 개설"
        title1="간편한 입금 출금,"
        title2="편하게 이용하세요 !"
        subtitle="트래빈뱅크 입출금통장"
        imageSrc={bean}
        logoSrc={logo}
        buttonText="시작하기"
        buttonPath="/creation/bank"
      />
    );
  }

  return (
    <CreationMain
      navText="여행 통장 개설"
      title1="함께 하는 여행,"
      title2="함께 모아보세요"
      subtitle="트래빈뱅크 여행통장"
      imageSrc={beans}
      logoSrc={logo}
      buttonText="시작하기"
      buttonPath="/creation/bank"
    />
  );
};

export default CreationMainPage;
