import React from "react";

import { useNavigate } from "react-router-dom";

import logo from "../../assets/logo.png";

interface NavBarProps {
  text?: string;
}

const NavBar: React.FC<NavBarProps> = ({ text }) => {
  const nav = useNavigate();

  // 로고 클릭 시 메인 페이지로 이동
  const handleLogoClick = () => {
    window.location.href = "http://localhost:3000/";
  };

  return (
    <div className="flex py-4">
      {/* 뒤로가기 */}
      <div>
        <button type="button" onClick={() => nav(-1)}>
          ←
        </button>
      </div>

      {/* 제목 or 로고 */}
      <div className="flex-grow text-center">
        {text ? (
          <div>{text}</div>
        ) : (
          <button type="button" onClick={handleLogoClick}>
            <img src={logo} alt="logo" className="h-5 mb-3" />
          </button>
        )}
      </div>
    </div>
  );
};

NavBar.defaultProps = {
  text: "",
};

export default NavBar;
