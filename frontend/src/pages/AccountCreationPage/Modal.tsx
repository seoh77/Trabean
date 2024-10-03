import React from "react";

interface SubMessage {
  key: string | number; // 고유 key 값
  text: string; // 메시지 텍스트
  className?: string; // 개별 스타일 클래스명 (선택적)
}

interface ModalProps {
  message: string;
  subMessage?: SubMessage[];
  onClose: () => void;
}

const Modal: React.FC<ModalProps> = ({ message, subMessage, onClose }) => (
  <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div className="bg-white rounded-xl p-7 shadow-lg max-w-lg text-center relative">
      <button
        type="button"
        className="absolute top-1 right-3 text-gray-500 hover:text-gray-800 text-2xl"
        onClick={onClose}
      >
        ×
      </button>
      <div className="flex items-center justify-center mb-4">
        <div className="bg-black text-white rounded-full w-6 h-6 flex items-center justify-center mt-5">
          <span className="text-lg font-bold text-white">!</span>
        </div>
      </div>
      <h2 className="text-md font-medium">{message}</h2>
      {subMessage && subMessage.length > 0 && (
        <div className="mb-3">
          {subMessage.map((msg) => (
            <p
              key={msg.key}
              className={msg.className || "text-xs text-gray-600"}
            >
              {msg.text}
            </p>
          ))}
        </div>
      )}
    </div>
  </div>
);

Modal.defaultProps = {
  subMessage: [],
};

export default Modal;
