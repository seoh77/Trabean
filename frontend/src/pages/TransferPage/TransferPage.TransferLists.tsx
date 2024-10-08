import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import beanProfile from "../../assets/bean_profile.png";
// import CustomKeypad from "../AccountCreationPage/Keypad";
interface Transfer {
  id: number;
  name: string;
  bank: string;
  account: string;
}

const transfers: Transfer[] = [
  {
    id: 1,
    name: "김민채",
    bank: "농협은행",
    account: "356-0630-5770-33",
  },
  {
    id: 2,
    name: "서희",
    bank: "국민은행",
    account: "123-4567-8901-23",
  },
  {
    id: 3,
    name: "김인실",
    bank: "신한은행",
    account: "110-2345-6789-00",
  },
  {
    id: 4,
    name: "남윤희",
    bank: "우리은행",
    account: "1002-345-678901",
  },
  {
    id: 5,
    name: "육민우",
    bank: "하나은행",
    account: "620-1234-5678-90",
  },
  {
    id: 6,
    name: "박세건",
    bank: "카카오뱅크",
    account: "3333-09-1234567",
  },
];

const TransferLists: React.FC = () => {
  const [selectedAccount, setSelectedAccount] = useState<Transfer | null>(null);
  const [manualInput, setManualInput] = useState<string>("");
  const navigate = useNavigate();

  // 계좌 선택
  const handleAccountSelect = (transfer: Transfer) => {
    setSelectedAccount(transfer);
    setManualInput(""); // 수동 입력 초기화
  };

  // 수동 계좌 입력 처리
  const handleManualInput = (e: React.ChangeEvent<HTMLInputElement>) => {
    setManualInput(e.target.value);
    setSelectedAccount(null); // 선택된 계좌 초기화
  };

  // 확인 버튼 클릭 시 다음 페이지로 이동
  const handleConfirm = () => {
    if (selectedAccount) {
      // 선택된 계좌가 있을 경우 그 계좌 정보를 전송
      navigate(`/transfer/list/${selectedAccount.account}`, {
        state: selectedAccount,
      });
    } else if (manualInput) {
      // 수동 입력된 계좌번호만 전송
      navigate(`/transfer/list/${manualInput}`);
    } else {
      alert("계좌 번호를 선택하거나 입력해 주세요.");
    }
  };

  return (
    <div className="p-4">
      <h1 className="text-2xl font-bold mb-6">이체</h1>
      {/* 직접 입력 */}
      <input
        type="text"
        placeholder="계좌 번호 직접 입력"
        value={manualInput}
        onChange={handleManualInput}
        className="w-full px-3 py-2 border rounded-lg mb-4"
      />

      {/* 확인 버튼 */}
      <button
        type="button"
        onClick={handleConfirm}
        className="mb-6 w-full bg-primary text-white py-3 text-lg font-semibold"
      >
        확인
      </button>

      {/* 계좌 목록 */}
      <ul className="space-y-3 mb-4">
        {transfers.map((transfer) => (
          <li
            key={transfer.id}
            className={`flex items-center p-3 rounded-lg shadow-sm cursor-pointer ${
              selectedAccount?.account === transfer.account
                ? "bg-green-100"
                : "bg-gray-50"
            }`}
            role="presentation"
            onClick={() => handleAccountSelect(transfer)}
          >
            <div className="flex-shrink-0 w-10 h-10 rounded-full bg-green-100 flex items-center justify-center">
              <img
                src={beanProfile}
                alt="profile"
                className="w-full h-full object-cover rounded-full"
              />
            </div>
            <div className="ml-3">
              <div className="font-semibold text-gray-800">{transfer.name}</div>
              <div className="text-gray-600">
                {transfer.bank} {transfer.account}
              </div>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default TransferLists;
