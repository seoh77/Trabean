// NextStepButton.tsx
import React from "react";

interface NextStepButtonProps {
  isEnabled: boolean;
  onClick: () => void;
  text: string;
}

const NextStepButton: React.FC<NextStepButtonProps> = ({
  isEnabled,
  onClick,
  text,
}) => (
  <button
    type="button"
    className={`w-4/5 p-2 text-lg font-medium rounded-full ${
      isEnabled
        ? "bg-primary text-white"
        : "bg-gray-400 text-white cursor-not-allowed"
    }`}
    disabled={!isEnabled}
    onClick={onClick}
  >
    {text}
  </button>
);

export default NextStepButton;
