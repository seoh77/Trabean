import React from "react";

const Alarm: React.FC = () => {
  // 알림 클릭 시 알림 페이지로 이동
  const handleAlarmClick = () => {
    console.log("알림 누름");
  };

  return (
    <div className="text-right p-4">
      <button type="button" onClick={handleAlarmClick}>
        알림
      </button>
    </div>
  );
};

export default Alarm;
