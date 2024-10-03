import React from "react";
import { useNavigate } from "react-router-dom"; // useNavigate 가져오기

interface NavBarProps {
  text: string;
}

const NavBar: React.FC<NavBarProps> = ({ text }) => {
  const navigate = useNavigate(); // navigate 훅 사용

  return (
    <div>
      <div className="flex pb-8 bg-white">
        <button
          type="button"
          className="focus:outline-none"
          onClick={() => navigate(-1)} // 이전 페이지로 이동
        >
          ←
        </button>
        <div className="flex-grow text-center text-sm">{text}</div>
      </div>
    </div>
  );
};

export default NavBar;
