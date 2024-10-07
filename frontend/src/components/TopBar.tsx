import React from "react";
import { useNavigate } from "react-router-dom";
import classNames from "classnames";
import logo from "../assets/logo.png";

interface topBar {
  isLogo: boolean;
  // eslint-disable-next-line react/require-default-props
  page?: string;
  // eslint-disable-next-line react/require-default-props
  path?: string;
  isWhite: boolean;
}

const TopBar: React.FC<topBar> = ({ isLogo, page, isWhite, path }) => {
  const navigate = useNavigate();

  // 배경색 클래스 설정
  const topBarClass = classNames(
    "w-[360px] h-[52px] flex items-center fixed top-0 z-70 text-gray-700",
    {
      "bg-[#F4F4F5]": !isWhite,
      "bg-white": isWhite,
    },
  );

  const goNavigate = () => {
    if (path === "" || path === null || path === undefined) {
      navigate(-1);
    } else {
      navigate(path);
    }
  };

  return (
    <div
      className={topBarClass}
      style={{
        boxShadow: "0 4px 6px -1px rgba(0, 0, 0, 0.1)",
      }}
    >
      <p className="text-lg px-[10px] relative text-center w-full font-semibold flex items-center justify-center">
        {/* 뒤로가기 버튼 */}
        <span
          className="absolute left-3 cursor-pointer"
          onClick={goNavigate}
          role="presentation"
        >
          ←
        </span>
        {/* 현재 페이지 정보 */}
        {!isLogo && page}
        {isLogo && (
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

export default TopBar;
