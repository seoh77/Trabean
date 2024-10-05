import React, { useState } from "react";
import client from "../../client"; // API 호출을 위한 client

interface ChangeAccountNameModalProps {
  accountId: string | undefined; // 여행통장의 ID
  accountName: string | undefined; // 기존 여행통장 이름
  onClose: () => void; // 모달을 닫는 함수
  onAccountNameChange: (updatedAccountName: string) => void; // 변경된 이름을 부모로 전달하는 함수
}

const ChangeAccountNameModal: React.FC<ChangeAccountNameModalProps> = ({
  accountId,
  accountName,
  onClose,
  onAccountNameChange,
}) => {
  const [newAccountName, setNewAccountName] = useState(accountName); // 변경할 이름을 상태로 관리

  // 입력 값이 변경될 때 상태 업데이트
  const handleAccountNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setNewAccountName(e.target.value);
  };

  // 확인 버튼을 눌렀을 때 서버로 PUT 요청 전송
  const handleSubmit = async () => {
    if (!newAccountName) {
      alert("계좌 이름을 입력하세요.");
      return;
    }

    try {
      // 서버로 PUT 요청을 통해 이름 변경
      await client().put("/api/travel/change/accountName", {
        accountId,
        accountName: newAccountName,
      });
      onAccountNameChange(newAccountName); // 부모 컴포넌트로 변경된 이름 전달
      alert("계좌 이름이 변경되었습니다.");
      onClose(); // 모달 닫기
    } catch (error) {
      alert("계좌 이름 변경 요청에 실패했습니다.");
    }
  };

  return (
    <div className="bg-white p-6 rounded-3xl">
      {" "}
      {/* 모달 스타일 */}
      <h2 className="text-xl mb-4">여행통장 이름 변경</h2>
      <input
        type="text"
        value={newAccountName} // 인풋 필드에 현재 이름 표시
        onChange={handleAccountNameChange} // 입력값 변경 시 호출
        placeholder="새로운 이름을 입력하세요"
        className="border p-2 w-full mb-4" // 스타일 설정
      />
      <div className="flex justify-between">
        <button
          type="button"
          onClick={onClose} // 취소 버튼 클릭 시 모달 닫기
          className="btn-light-lg w-1/2 mr-2 text-center"
        >
          취소
        </button>
        <button
          type="button"
          onClick={handleSubmit} // 확인 버튼 클릭 시 서버 요청
          className="btn-lg w-1/2 ml-2 text-center"
        >
          확인
        </button>
      </div>
    </div>
  );
};

export default ChangeAccountNameModal;
