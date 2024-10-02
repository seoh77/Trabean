import React from "react";
import smileImage from "../../assets/bean_profile.png";

const transfers = [
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

const App = () => (
  <div className="App">
    <div className="p-4">
      <h1 className="text-2xl font-bold mb-4">이체</h1>
      <input
        type="text"
        placeholder="계좌번호 직접 입력"
        className="w-full px-3 py-2 border rounded-lg mb-4"
      />
      <h2 className="text-xl font-semibold mb-2">최근 이체</h2>
      <ul className="space-y-3">
        {transfers.map((transfer) => (
          <li
            key={transfer.id}
            className="flex items-center bg-gray-50 p-3 rounded-lg shadow-sm"
          >
            <img
              src={smileImage}
              alt="Smile"
              className="w-10 h-10 rounded-full"
            />
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
  </div>
);

export default App;
