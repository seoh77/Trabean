import React, { useEffect } from "react";
import bean from "../../assets/bean_chat.png";

interface Message {
  key: string;
  type: "user" | "bot";
  content: string;
}

interface ChatMessagesProps {
  messages: Message[];
  messagesEndRef: React.RefObject<HTMLDivElement>;
}

const ChatMessages: React.FC<ChatMessagesProps> = ({
  messages,
  messagesEndRef,
}) => {
  useEffect(() => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [messages, messagesEndRef]);

  return (
    <div className="flex flex-col space-y-4">
      {messages.map((msg) => (
        <div
          key={msg.key}
          className={`flex ${msg.type === "user" ? "justify-end" : "items-start"}`}
        >
          {msg.type === "bot" && (
            <img
              src={bean}
              alt="bot-profile"
              className="w-8 h-8 rounded-full mr-2"
            />
          )}
          <div
            style={{ whiteSpace: "pre-line" }}
            className={`text-sm shadow-lg mt-2 px-4 py-2 max-w-xs ${
              msg.type === "user"
                ? "bg-primary text-white rounded-tl-lg rounded-bl-lg rounded-br-lg"
                : "bg-white text-gray-800 rounded-tr-lg rounded-bl-lg rounded-br-lg"
            }`}
          >
            {msg.content}
          </div>
        </div>
      ))}
      <div ref={messagesEndRef} />
    </div>
  );
};

export default ChatMessages;
