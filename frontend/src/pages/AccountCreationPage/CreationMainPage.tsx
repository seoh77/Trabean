import React, { useState } from "react";
import { useAccountType } from "./AccountTypeContext";
import TopBar from "../../components/TopBar";
import CreationMain from "./CreationMain";
import beans from "../../assets/beans.png";
import bean from "../../assets/bean_personal.png";
import logo from "../../assets/logo.png";

const CreationMainPage: React.FC = () => {
  const { accountType, setAccountType } = useAccountType();
  const [isSelectedType, setIsSelectedType] = useState<boolean>(false);

  const selectedType = (selected: "travel" | "personal") => {
    setAccountType(selected);
    setIsSelectedType(true);
  };

  if (!isSelectedType) {
    return (
      <div className="flex flex-col justify-center items-center h-screen space-y-6">
        <TopBar isLogo={false} page="통장 개설" isWhite />
        <div className="text-xl font-semibold">
          개설할 통장의 유형을 선택해 주세요
        </div>
        <div className="flex flex-col space-y-8">
          <button
            type="button"
            className="shadow-lg w-72 h-40 bg-primary-light p-2 rounded-2xl flex flex-col justify-center items-center hover:bg-[#90C183]"
            onClick={() => selectedType("travel")}
          >
            <img
              src={beans}
              alt="여행 통장"
              className="mx-auto max-h-32 max-w-64 mb-2"
            />
            <div className="font-semibold">다같이 함께하는 여행 통장</div>
          </button>
          <button
            type="button"
            className="shadow-lg w-72 h-40 bg-primary-light p-2 rounded-2xl flex flex-col justify-center items-center hover:bg-[#90C183]"
            onClick={() => selectedType("personal")}
          >
            <img
              src={bean}
              alt="개인 통장"
              className="mx-auto max-h-24 max-w-64 mb-2"
            />
            <div className="font-semibold">혼자서도 간편한 개인 통장</div>
          </button>
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
