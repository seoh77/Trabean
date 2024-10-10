/* eslint-disable no-shadow */
import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom"; // useNavigate 추가
import beanProfile from "../../assets/bean_profile.png";
import trabeanLogo from "../../assets/logo.png";
import { formatNumberWithCommas } from "../../utils/formatNumber";
import TopBar from "../../components/TopBar";
import deleteIcon from "../../assets/deleteIcon.png";
import client from "../../client";

interface TransferDetails {
  id?: number;
  name?: string;
  bank?: string;
  account: string;
}

const TransferList: React.FC = () => {
  const [accountName, setAccountName] = useState<number | null>(null);
  const location = useLocation();
  const { account } = location.state || {}; // URL에서 계좌 번호 받기
  const navigate = useNavigate(); // useNavigate 훅
  const transferDetails = location.state as TransferDetails; // 이전 페이지에서 전달된 state 받기
  const [amount, setAmount] = useState<string>(""); // 송금 금액 관리
  const [isModalOpen, setIsModalOpen] = useState(false); // 모달 상태 관리
  const { accountId } = location.state || {};
  // const [depositAccountNo, setDepositAccountNo] = useState<string | null>(null);
  const [withdrawalAccountNo, setWithdrawalAccountNo] = useState<string | null>(
    null,
  );
  // 숫자 클릭 처리
  const handleNumberClick = (value: string) => {
    setAmount((prev) => prev + value);
  };

  // 금액 지우기 처리
  const handleClear = () => {
    setAmount("");
  };

  // 모달 열기
  const handleSend = () => {
    setIsModalOpen(true);
  };

  // const getDepositAccountNo = () => {
  //   setDepositAccountNo(account);
  // };
  const getWithdrawalAccountNo = async () => {
    const body = JSON.stringify({
      accountId,
      // accountName,
      // targetAmount: parseInt(targetAmount, 10), // 목표 금액을 정수형으로 변환
    });

    // API 요청 전송
    const response = await client().post(
      "/api/accounts/internal/get-accountNo",
      body,
    );
    // console.log(response.data.accountNo, 2);
    // console.log(response.data["accountNo"], 2);
    setWithdrawalAccountNo(response.data.accountNo);
    console.log(response.data.accountNo);
  };
  // 모달 닫기
  const handleCloseModal = () => {
    setIsModalOpen(false);
  };
  // 비밀번호 입력 페이지로 이동
  const handleConfirm = () => {
    // getDepositAccountNo();
    getWithdrawalAccountNo();
    // console.log(withdrawalAccountNo, 3);
    navigate(`/transfer/password/${accountId}`, {
      state: {
        amount,
        accountId,
        depositAccountNo: account,
        withdrawalAccountNo,
      },
    }); // 비밀번호 입력 페이지로 이동
  };

  const handleDelete = () => {
    setAmount(amount.slice(0, -1));
  };
  const getAccountName = async () => {
    try {
      const response = await client().get(`/api/accounts/${account}/name`);
      setAccountName(response.data.name);
    } catch (error) {
      console.error("main payment account 불러올 때 에러 발생:", error);
    }
  };

  useEffect(() => {
    getWithdrawalAccountNo();
    getAccountName();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);
  return (
    <div>
      <TopBar isLogo={trabeanLogo} page="계좌 이체" isWhite />
      <div className="px-6 py-20 bg-white">
        {/* 프로필 및 계좌 정보 */}
        <div className="flex items-center mb-6 justify-start">
          <div className="w-12 h-12 rounded-full bg-green-100 flex items-center ml-8">
            <img
              src={beanProfile}
              alt="profile"
              className="w-full h-full m-0 object-cover rounded-full"
            />
          </div>
          <div className="ml-5 text-left">
            <p className="text-lg font-semibold">
              {transferDetails?.name || accountName}
            </p>
            <p className="text-sm text-gray-600">
              {transferDetails?.bank || "트래빈뱅크"} {account}
            </p>
          </div>
        </div>

        {/* 금액 입력 */}
        <div className="w-full flex justify-between text-center items-center">
          <span className="ml-10 mb-0 text-2xl flex font-bold items-center justify-center">
            {formatNumberWithCommas(parseInt(amount, 10)) || "0"} 원
          </span>
          <button type="button" onClick={handleClear} className="mr-2">
            <img src={deleteIcon} alt="clear" className="w-6 h-6" />
          </button>
        </div>

        {/* 입력 밑줄 */}
        <div className="border-b-2 border-gray-300 w-full mb-10"> </div>

        {/* 송금 버튼 */}
        <button
          type="button"
          className="w-full max-w-md bg-primary text-white py-3 mt-4 text-lg font-semibold"
          onClick={handleSend}
          disabled={!amount || parseInt(amount, 10) === 0}
        >
          송금
        </button>
        {/* 숫자 키패드 그리드 */}
        <div className="grid grid-cols-3 gap-4">
          {[1, 2, 3, 4, 5, 6, 7, 8, 9].map((number) => (
            <button
              type="button"
              key={number}
              onClick={() => handleNumberClick(number.toString())}
              className="w- h-16 text-md"
            >
              {number}
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
            onClick={() => handleDelete()}
            className="w-24 h-16 text-md rounded-full"
          >
            ⌫
          </button>
        </div>

        {/* 모달 */}
        {isModalOpen && (
          <div className="absolute inset-0 flex items-center justify-center bg-black bg-opacity-50">
            <div className="bg-white p-6 rounded-lg shadow-lg w-full max-w-xs">
              {/* 모달의 너비를 적절히 조정 */}
              <div className="mb-4 text-center">
                <div className="font-semibold text-lg">
                  {transferDetails?.name || `${accountName} `}
                  님께
                </div>
                <div>송금하시겠습니까?</div>
              </div>
              <div className="flex justify-around space-x-4">
                {/* 버튼 간의 간격 추가 */}
                <button
                  type="button"
                  className="bg-gray-200 py-2 px-4 rounded-lg"
                  onClick={handleCloseModal}
                >
                  취소
                </button>
                <button
                  type="button"
                  className="bg-green-500 text-white py-2 px-4 rounded-lg"
                  onClick={handleConfirm} // 확인 버튼 클릭 시 비밀번호 입력 페이지로 이동
                >
                  확인
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default TransferList;
