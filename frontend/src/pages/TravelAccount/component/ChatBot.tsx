import React from "react";
import { useNavigate } from "react-router-dom";
import bean from "../../../assets/bean.png";

const ChatBot: React.FC = () => {
  const navigate = useNavigate();
  const handleChatBotClick = () => {
    navigate("/chatbot");
  };

  return (
    <div className="flex justify-end p-4">
      <button type="button" onClick={handleChatBotClick}>
        <div className="w-16 h-16 bg-white rounded-full flex items-center justify-center">
          <img src={bean} alt="bean" className="w-12 h-12" />
        </div>
      </button>
    </div>
  );
};

export default ChatBot;
