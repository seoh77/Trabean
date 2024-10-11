import React from "react";
import { useNavigate } from "react-router-dom";
import NavBar from "./NavBar";
import NextStepButton from "./NextStepButton";
import TopBar from "../../components/TopBar";

interface ReusableMainPageProps {
  navText: string; // 네비게이션 바의 텍스트
  subtitle: string; // 서브 타이틀
  title1: string; // 상단 제목
  title2: string; // 상단 제목
  imageSrc: string; // 메인 이미지 경로
  logoSrc: string; // 브랜드 로고 이미지 경로
  buttonText: string; // 버튼 텍스트
  buttonPath: string; // 버튼 클릭 시 이동할 경로
}

const ReusableMainPage: React.FC<ReusableMainPageProps> = ({
  navText,
  subtitle,
  title1,
  title2,
  imageSrc,
  logoSrc,
  buttonText,
  buttonPath,
}) => {
  const navigate = useNavigate();

  return (
    <div>
      <TopBar isLogo={false} page="통장 개설" isWhite />

      <div className="px-6 py-8 bg-white">
        <NavBar text={navText} />

        <div className="flex flex-col items-center justify-center bg-white px-6 mt-10">
          {/* 서브 타이틀 */}
          <p className="text-center text-gray-500 mb-2">{subtitle}</p>

          {/* 메인 타이틀 */}
          <h2 className="text-center text-2xl font-semibold">{title1}</h2>
          <h2 className="text-center text-2xl font-semibold mb-10">{title2}</h2>

          {/* 이미지 영역 */}
          <div className="mb-2">
            <img
              src={imageSrc}
              alt="Main Visual"
              className="max-w-68 max-h-32"
            />
          </div>

          {/* 브랜드 로고 */}
          <div className="mb-10">
            <img src={logoSrc} alt="Brand Logo" className="w-24 h-auto" />
          </div>

          {/* 시작하기 버튼 */}
          <div className="flex justify-center mt-10 w-full">
            <NextStepButton
              isEnabled
              onClick={() => navigate(buttonPath)}
              text={buttonText}
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default ReusableMainPage;
