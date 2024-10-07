import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
// import NavBar from "./NavBar";
import NextStepButton from "./NextStepButton";
import TopBar from "../../components/TopBar";
import nhIcon from "../../assets/bankIcon/nhIcon.png";
import kbIcon from "../../assets/bankIcon/kbIcon.png";
import shinhanIcon from "../../assets/bankIcon/shIcon.png";
import wooriIcon from "../../assets/bankIcon/wooriIcon.png";
import hanaIcon from "../../assets/bankIcon/hanaIcon.png";
import ibkIcon from "../../assets/bankIcon/ibkIcon.png";
import scIcon from "../../assets/bankIcon/scIcon.png";
import citiIcon from "../../assets/bankIcon/citiIcon.png";
import kakaoIcon from "../../assets/bankIcon/kakaoIcon.png";
import saemaeulIcon from "../../assets/bankIcon/saemaeulIcon.png";
import daeguIcon from "../../assets/bankIcon/daeguIcon.png";
import gwangjuIcon from "../../assets/bankIcon/gwangjuIcon.png";
import postIcon from "../../assets/bankIcon/postIcon.png";
import sinIcon from "../../assets/bankIcon/sinIcon.png";
import busanIcon from "../../assets/bankIcon/busanIcon.png";

const BankSelection: React.FC = () => {
  const [selectedBank, setSelectedBank] = useState<string | null>(null);
  const navigate = useNavigate(); // useNavigate 훅 사용

  const banks = [
    { name: "NH농협", logo: nhIcon },
    { name: "KB국민", logo: kbIcon },
    { name: "신한", logo: shinhanIcon },
    { name: "우리", logo: wooriIcon },
    { name: "하나", logo: hanaIcon },
    { name: "IBK기업", logo: ibkIcon },
    { name: "SC제일", logo: scIcon },
    { name: "씨티", logo: citiIcon },
    { name: "카카오뱅크", logo: kakaoIcon },
    { name: "새마을", logo: saemaeulIcon },
    { name: "대구", logo: daeguIcon },
    { name: "광주", logo: gwangjuIcon },
    { name: "우체국", logo: postIcon },
    { name: "신협", logo: sinIcon },
    { name: "부산", logo: busanIcon },
  ];

  const handleNextStep = () => {
    if (selectedBank) {
      // 선택한 은행 정보와 함께 새 페이지로 이동
      sessionStorage.setItem("isSelectedBank", "true");
      navigate(`/creation/account?bank=${selectedBank}`);
    }
  };

  return (
    <div>
      <TopBar isLogo={false} page="통장 개설" isWhite />
      <div className="px-6 py-20 bg-white">
        <div className="text-left mb-5">
          <p>
            통장 개설을 위해 <strong>실명 인증</strong>이 필요합니다.
          </p>
          <p className="font-bold mt-3">
            1원 인증을 통해 실명 인증을 진행합니다.
          </p>
          <p className="text-xs text-gray-500">
            본인이 맞는지 확인해볼게요. <br />
            주로 쓰는 은행 계좌를 선택해주세요.
          </p>
        </div>

        <div className="grid grid-cols-3 gap-3 mb-5">
          {banks.map((bank) => (
            <button
              key={bank.name}
              type="button"
              className={`py-2.5 rounded-lg cursor-pointer transition-colors duration-200 ${
                selectedBank === bank.name
                  ? "bg-primary-light"
                  : "bg-zinc-100 hover:bg-zinc-300"
              }`}
              onClick={() => {
                setSelectedBank(bank.name);
              }}
            >
              <img
                src={bank.logo}
                alt={`${bank.name} 로고`}
                className="w-6 h-6 mb-2 mx-auto object-contain"
              />
              <span>{bank.name}</span>
            </button>
          ))}
        </div>

        <div className="flex justify-center">
          <NextStepButton
            isEnabled={!!selectedBank}
            onClick={handleNextStep}
            text="다음 단계"
          />
        </div>
      </div>
    </div>
  );
};

export default BankSelection;
