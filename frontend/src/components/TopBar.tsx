import React from "react";
import { useLocation, useNavigate } from "react-router-dom";
import classNames from "classnames";

const NavBar: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();

  // 현재 경로로부터 페이지 이름 추출 (단순히 경로명을 표시할 수 있음)
  const currentPageName = location.pathname.split("/").pop() || "";

  // 경로와 페이지 명 매핑
  // 1. 네비게이션 바 넣을 때 본인이 알아서 매핑 해주세요
  // 2. 배경색 흰색 아닐경우 밑에 리스트에도 추가해주세요
  const pageNames: { [key: string]: string } = {
    "": "Loading",
    qr: "QR 결제",
    list: "가계부",
  };

  // 배경색이 흰색이 아닌 페이지 리스트 (여기에 추가)
  const notWhiteBackGround: string[] = ["list"];

  // 배경색 클래스 설정
  const topBarClass = classNames(
    "w-[360px] h-[52px] flex items-center fixed top-0 shadow-md z-100",
    {
      "bg-[#F4F4F5]": notWhiteBackGround.includes(currentPageName),
      "bg-white": !notWhiteBackGround.includes(currentPageName),
    },
  );

  return (
    <div className={topBarClass}>
      <p className="text-lg px-[10px] relative text-center w-full font-semibold">
        {/* 뒤로가기 버튼 */}
        <span
          className="absolute left-3 cursor-pointer"
          onClick={() => navigate(-1)}
          role="presentation"
        >
          ←
        </span>
        {/* 현재 페이지 정보 */}
        {pageNames[currentPageName] || "페이지"}
      </p>
    </div>
  );
};

export default NavBar;
