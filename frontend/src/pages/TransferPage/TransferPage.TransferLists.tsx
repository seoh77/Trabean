import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import beanProfile from "../../assets/bean_profile.png";
import client from "../../client";
import TopBar from "../../components/TopBar";
import trabeanLogo from "../../assets/logo.png";

interface Transfer {
  accountId: number; // 계좌 식별자
  accountNo: string; // 계좌번호
  adminName: string; // 예금주
  bankName: string; // 은행이름
}

const TransferLists: React.FC = () => {
  const [transfers, setTransfers] = useState<Transfer[]>([]); // API 응답으로 받아온 계좌 목록 상태
  const [selectedAccount, setSelectedAccount] = useState<Transfer | null>(null);
  const [manualInput, setManualInput] = useState<string>("");
  const { accountId } = useParams<{ accountId: string }>();
  const navigate = useNavigate();

  // API로부터 계좌 목록 받아오기
  useEffect(() => {
    const fetchTransferList = async () => {
      try {
        const response = await client().get("/api/transfer/accounts");
        if (response.data && response.data.accountList) {
          setTransfers(response.data.accountList); // 응답 데이터에서 accountList를 상태에 저장
        }
      } catch (error) {
        console.error("Error fetching transfer list:", error);
      }
    };

    fetchTransferList();
  }, []);

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
      navigate(`/transfer/list/${accountId}/${selectedAccount.accountNo}`, {
        state: {
          account: selectedAccount.accountNo,
          name: selectedAccount.adminName,
          bank: selectedAccount.bankName,
          accountId,
          withdrawalAccountNo: selectedAccount.accountNo,
        },
      });
    } else if (manualInput) {
      // 수동 입력된 계좌번호만 전송
      navigate(`/transfer/list/${accountId}/${manualInput}`, {
        state: {
          account: manualInput,
          bank: "트래빈뱅크",
          accountId,
        },
      });
    } else {
      alert("계좌 번호를 선택하거나 입력해 주세요.");
    }
  };

  return (
    <div>
      <TopBar isLogo={trabeanLogo} page="계좌 이체" isWhite />
      <div className="px-6 py-20 bg-white">
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
              key={transfer.accountId}
              className={`flex items-center p-3 rounded-lg shadow-sm cursor-pointer ${
                selectedAccount?.accountId === transfer.accountId
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
                <div className="font-semibold text-gray-800">
                  {transfer.adminName}
                </div>
                <div className="text-gray-600">
                  {transfer.bankName} {transfer.accountNo}
                </div>
              </div>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default TransferLists;
