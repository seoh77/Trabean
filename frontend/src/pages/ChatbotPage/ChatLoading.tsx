import React from "react";
import earth from "../../assets/earth.png";
import plane from "../../assets/plane.png"; // 태양 이미지라고 가정하고 사용

const RotatingEarth: React.FC = () => (
  <div className="flex flex-col items-center justify-center h-screen bg-white">
    <div className="font-bold text-center text-xl">✈️ 비니와 함께하는 여행</div>
    {/* 로딩 이미지 */}
    <div className="relative w-96 h-96">
      <img
        src={plane}
        alt="비니"
        className="w-96 h-96 animate-spin absolute"
        style={{
          animationDirection: "reverse",
          animationDuration: "5s",
          zIndex: 1,
        }}
      />
      {/* 중심에 위치한 지구 이미지 */}
      <img
        src={earth}
        alt="지구"
        className="w-96 h-96 absolute"
        style={{ zIndex: 2 }}
      />
    </div>
    {/* 하단 텍스트 */}
    <div className="bg-primary text-white text-center text-sm font-medium px-4 py-2 rounded-full">
      비니가 최고의 여행 경로를 찾고있어요!
    </div>
  </div>
);

export default RotatingEarth;
