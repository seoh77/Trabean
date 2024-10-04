import React from "react";
import { useLocation, useNavigate } from "react-router-dom";
import classNames from "classnames";
import logo from "../assets/logo.png";

const NavBar: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();

  // 현재 경로로부터 페이지 이름 추출 (단순히 경로명을 표시할 수 있음)
  const currentPageName = location.pathname.split("/").pop() || "";
  console.log(currentPageName);
  // 경로와 페이지 명 매핑
  // 1. 네비게이션 바 넣을 때 본인이 알아서 매핑 해주세요
  // 2. 배경색 흰색 아닐경우 밑에 리스트에도 추가해주세요
  const pageNames: { [key: string]: string } = {
    "": "홈화면",
    qr: "QR 결제",
    list: "가계부",
  };

  // 배경색이 흰색이 아닌 페이지 리스트 (여기에 추가)
  const notWhiteBackGround: string[] = ["list"];

  // 로고를 쓰는 페이지 리스트 (여기에 추가)
  const logoPageList: string[] = [""];

  // 배경색 클래스 설정
  const topBarClass = classNames(
    "w-[360px] h-[52px] flex items-center fixed top-0 z-100 text-gray-700",
    {
      "bg-[#F4F4F5]": notWhiteBackGround.includes(currentPageName),
      "bg-white": !notWhiteBackGround.includes(currentPageName),
    },
  );

  return (
    <div
      className={topBarClass}
      style={{
        boxShadow: "0 4px 6px -1px rgba(0, 0, 0, 0.1)",
      }}
    >
      <p className="text-lg px-[10px] relative text-center w-full font-semibold flex items-center">
        {/* 뒤로가기 버튼 */}
        <span
          className="absolute left-3 cursor-pointer"
          onClick={() => navigate(-1)}
          role="presentation"
        >
          ←
        </span>
        {/* 현재 페이지 정보 */}
        {!logoPageList.includes(currentPageName) &&
          (pageNames[currentPageName] || "페이지")}
        {logoPageList.includes(currentPageName) && (
          <img
            src={logo}
            alt="trabean"
            className="w-[77px] h-[15px] absolute left-[40%]"
          />
        )}
      </p>
    </div>
  );
};

export default NavBar;
