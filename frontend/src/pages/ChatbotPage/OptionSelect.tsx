import React, { useState } from "react";

interface Option {
  id: string;
  description: string;
}

interface OptionsSelectProps {
  options: Option[];
  onOptionClick: (id: string, description: string) => void;
}

const OptionsSelect: React.FC<OptionsSelectProps> = ({
  options,
  onOptionClick,
}) => {
  // 선택된 옵션들을 관리하는 상태 추가
  const [selectedOptions, setSelectedOptions] = useState<Option[]>([]);

  // 버튼 클릭 시 호출되는 함수
  const handleOptionClick = (option: Option) => {
    // 선택된 옵션인지 확인
    const isOptionSelected = selectedOptions.some(
      (item) => item.id === option.id,
    );

    let updatedOptions;
    if (isOptionSelected) {
      // 이미 선택된 옵션이면 제거
      updatedOptions = selectedOptions.filter((item) => item.id !== option.id);
    } else {
      // 선택되지 않은 옵션이면 배열에 추가
      updatedOptions = [...selectedOptions, option];
    }

    // 선택된 옵션들을 업데이트
    setSelectedOptions(updatedOptions);
    onOptionClick(option.id, option.description); // 부모 컴포넌트로 클릭 이벤트 전달
  };

  return (
    <div>
      <div className="flex flex-wrap gap-2 mt-2">
        {/* 옵션 버튼들 렌더링 */}
        {options.map((option) => {
          const isSelected = selectedOptions.some(
            (item) => item.id === option.id,
          );
          return (
            <button
              key={option.id}
              type="button"
              className={`${
                isSelected ? "bg-green-500" : "bg-primary"
              } text-white text-xs px-4 py-2 rounded-xl shadow-sm hover:bg-primary-light hover:border border-white`}
              onClick={() => handleOptionClick(option)}
            >
              {option.description}
            </button>
          );
        })}
      </div>
    </div>
  );
};

export default OptionsSelect;
