import React from "react";

interface CancelButtonProps {
  onCancelClick: () => void; // 취소 버튼 클릭 시 실행할 함수
  onOkayClick: () => void; // 취소 버튼 클릭 시 실행할 함수
  cancle: boolean | null;
  ok: boolean | null;
}

const CancelButton: React.FC<CancelButtonProps> = ({
  onCancelClick,
  onOkayClick,
  cancle,
  ok,
}) => (
  <div className="flex justify-center w-full mt-4 flex-row items-center space-x-5">
    {/* 중앙 정렬을 위한 부모 div */}
    {cancle && (
      <button
        type="button"
        className="w-16 bg-gray-500 text-white text-xs px-4 py-2 rounded-xl shadow-sm hover:bg-red-600 border border-white"
        onClick={onCancelClick}
      >
        취소
      </button>
    )}

    {ok && (
      <button
        type="button"
        className="w-16 bg-[#97c28c] text-white text-xs px-4 py-2 rounded-xl shadow-sm hover:bg-primary border border-white"
        onClick={onOkayClick}
      >
        완료
      </button>
    )}
  </div>
);

export default CancelButton;
