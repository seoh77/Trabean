import React from "react";

interface ModalInputProps {
  text: string | number | null;
  holderText: string | undefined;
  isAmount: boolean | null;
  inputValue: string | number;
  setInputValue: (value: string) => void;
  inputType: string;
  startDay: string | undefined;
}

const ModalInput: React.FC<ModalInputProps> = ({
  text,
  holderText,
  isAmount = false,
  inputValue,
  setInputValue,
  inputType = "text",
  startDay,
}) => {
  // 입력값을 처리하여 형식 적용
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (inputType === "text") {
      let { value } = e.target;

      if (isAmount) {
        // 숫자 이외의 문자 제거 및 콤마 추가
        value = value.replace(/[^0-9]/g, ""); // 숫자가 아닌 모든 문자 제거
        const formattedValue = Number(value).toLocaleString(); // 숫자 형식에 맞게 콤마 추가
        setInputValue(formattedValue);
      } else {
        setInputValue(value);
      }
    } else if (inputType === "date") {
      const inputDate = e.target.value; // 입력된 날짜 (YYYY-MM-DD 형식)
      if (inputDate) {
        setInputValue(inputDate);
      }
    }
  };

  return (
    <div className="py-4 rounded-xl flex flex-col items-center justify-center bg-gray-100 mt-2 mx-5">
      {/* 상단 텍스트 영역 */}
      <div className="text-black text-lg font-medium mb-4">{text}</div>

      {/* 둥근 입력 필드 */}
      <input
        type={inputType}
        placeholder={holderText}
        value={inputValue}
        onChange={handleInputChange}
        className="bg-gray-200 px-5 h-10 text-black text-sm font-semibold rounded-full text-center focus:outline-none focus:border-green-700"
        min={startDay}
      />
    </div>
  );
};

export default ModalInput;
