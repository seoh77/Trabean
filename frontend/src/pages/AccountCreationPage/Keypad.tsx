import React from "react";
import beanOff from "../../assets/bean_off.png";
import beanProfile from "../../assets/bean_on.png";

// 키패드 Props 타입 정의
interface KeypadProps {
  password: string; // 입력된 비밀번호 상태
  onChange: (newPassword: string) => void; // 비밀번호 변경 핸들러
  onComplete: () => void; // 완료 버튼 클릭 시 호출되는 함수
}

const CustomKeypad: React.FC<KeypadProps> = ({
  password,
  onChange,
  onComplete,
}) => {
  // 키패드 버튼 클릭 핸들러
  const handleNumberClick = (value: string) => {
    if (password.length < 6) {
      onChange(password + value);
    }
  };

  // 삭제 버튼 클릭 핸들러
  const handleDelete = () => {
    onChange(password.slice(0, -1));
  };

  return (
    <div className="flex flex-col items-center">
      {/* 비밀번호 입력 상태 이미지 */}
      <div className="flex mb-16">
        {Array.from({ length: 6 }, (_, index) => (
          <img
            key={index}
            src={index < password.length ? beanProfile : beanOff}
            alt="bean"
            className="w-10 h-10 mx-1"
          />
        ))}
      </div>

      {/* 완료 버튼 */}
      <button
        type="button"
        onClick={onComplete}
        className="w-full h-10 bg-primary text-white mb-2"
        disabled={password.length < 6}
      >
        완료
      </button>

      {/* 숫자 키패드 그리드 */}
      <div className="grid grid-cols-3 gap-4">
        {[1, 2, 3, 4, 5, 6, 7, 8, 9].map((num) => (
          <button
            type="button"
            key={num}
            onClick={() => handleNumberClick(num.toString())}
            className="w-24 h-16 text-md"
          >
            {num}
          </button>
        ))}
        {/* 특수 버튼 (00, 0, 삭제) */}
        <button
          type="button"
          onClick={() => handleNumberClick("00")}
          className="w-24 h-16 text-md rounded-full"
        >
          00
        </button>
        <button
          type="button"
          onClick={() => handleNumberClick("0")}
          className="w-24 h-16 text-md rounded-full"
        >
          0
        </button>
        <button
          type="button"
          onClick={handleDelete}
          className="w-24 h-16 text-md rounded-full"
        >
          ⌫
        </button>
      </div>
    </div>
  );
};

export default CustomKeypad;
