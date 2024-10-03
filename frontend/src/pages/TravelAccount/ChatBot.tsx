import React from "react";

import bean from "../../assets/bean.png";

const ChatBot: React.FC = () => {
  // 챗봇 클릭 시 챗봇 페이지로 이동
  const handleChatBotClick = () => {
    console.log("챗봇 누름");
  };

  return (
    <div className="flex justify-end p-4">
      <button type="button" onClick={handleChatBotClick}>
        <img src={bean} alt="bean" className="w-10 h-10" />
      </button>
    </div>
  );
};

export default ChatBot;
