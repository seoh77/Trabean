import React from "react";

interface ChatInputProps {
  userInput: string;
  sendDisable: boolean;
  onInputChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onSendMessage: () => void;
}

const ChatInput: React.FC<ChatInputProps> = ({
  userInput,
  sendDisable,
  onInputChange,
  onSendMessage,
}) => (
  <div className="w-[360px] h-[60px] flex fixed bottom-0 left-[calc(50%+10px)] transform translate-x-[-50%] p-2 bg-white shadow-2xl space-x-2 z-50">
    <input
      disabled={sendDisable}
      type="text"
      value={userInput}
      onChange={onInputChange}
      className="flex-grow border border-gray-300 rounded-lg px-4 py-2 text-sm"
      placeholder="메시지를 입력하세요..."
    />
    <button
      type="button"
      className="bg-primary text-white px-4 py-2 rounded-lg shadow-sm"
      onClick={onSendMessage}
      disabled={!userInput.trim() || sendDisable} // 입력이 없으면 비활성화
    >
      전송
    </button>
  </div>
);

export default ChatInput;
