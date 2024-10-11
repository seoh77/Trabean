import React from "react";

interface Option {
  id: string;
  description: string;
}

interface OptionsPanelProps {
  options: Option[];
  onOptionClick: (id: string, description: string) => void;
}

const OptionsPanel: React.FC<OptionsPanelProps> = ({
  options,
  onOptionClick,
}) => (
  // mode에 따른 클릭 이벤트 핸들러 정의

  <div>
    <div className="flex flex-wrap gap-2 mt-2">
      {/* 옵션 버튼들 렌더링 */}
      {options.map((option) => (
        <button
          key={option.id}
          type="button"
          className="bg-primary text-white text-xs px-4 py-2 rounded-xl shadow-sm hover:bg-primary-light hover:border border-white"
          onClick={() => onOptionClick(option.id, option.description)} // 핸들러 함수 호출
        >
          {option.description}
        </button>
      ))}
    </div>
  </div>
);
export default OptionsPanel;
