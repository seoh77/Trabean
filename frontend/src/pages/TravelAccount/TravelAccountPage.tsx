import React from "react";

import test from "../../assets/test.png";

const TravelAccountPage: React.FC = () => (
  <div className="h-full bg-red-500">
    <div>
      <div>←</div>
      <div>멘트</div>
    </div>

    <div>상단</div>

    <div>중단</div>

    <div className="flex justify-evenly bg-fuchsia-400">
      <div className="flex flex-col items-center bg-slate-400">
        <div>
          <img className="w-10 h-10" src={test} alt="이미지1" />
        </div>
        <div>친구들과</div>
        <div>N빵하기</div>
      </div>
      <div className="flex flex-col items-center bg-slate-400">
        <div>
          <img className="w-10 h-10" src={test} alt="이미지2" />
        </div>
        <div>다함께</div>
        <div>결제해요</div>
      </div>
      <div className="flex flex-col items-center bg-slate-400">
        <div>
          <img className="w-10 h-10" src={test} alt="이미지3" />
        </div>
        <div>예산관리</div>
        <div>가계부</div>
      </div>
    </div>
  </div>
);

export default TravelAccountPage;
