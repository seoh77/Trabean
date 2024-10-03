// Modal.tsx
import React from "react";

interface ModalProps {
  message: string; // 동적으로 전달받을 메시지
  subMessage?: string; // 선택적 하위 메시지
  onClose: () => void; // 모달 닫기 함수
}

const Modal: React.FC<ModalProps> = ({ message, subMessage, onClose }) => (
  <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div className="bg-white rounded-xl p-7 shadow-lg max-w-lg text-center relative">
      {" "}
      {/* relative 추가 */}
      <button
        type="button"
        className="absolute top-1 right-3 text-gray-500 hover:text-gray-800 text-2xl" // X 버튼 스타일
        onClick={onClose}
      >
        ×
      </button>
      <div className="flex items-center justify-center mb-4">
        <div className="bg-black text-white rounded-full w-6 h-6 flex items-center justify-center mt-5">
          <span className="text-lg font-bold text-white">!</span>
        </div>
      </div>
      <h2 className="text-md font-medium mb-2">{message}</h2>
      {subMessage &&
        subMessage.trim() && ( // 조건부 렌더링
          <p className="text-xs text-gray-600 mb-3">{subMessage}</p>
        )}
    </div>
  </div>
);

// defaultProps 추가
Modal.defaultProps = {
  subMessage: "", // 기본값 설정
};

export default Modal;
