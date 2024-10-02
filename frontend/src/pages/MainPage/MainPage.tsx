// import { useEffect, useState } from "react";

import Account from "./MainPage.Account";

import logo from "../../assets/logo.png";
import plusIcon from "../../assets/icon/plusIcon.png";
// import client from "../../client";

// type Account = {
//   accountId: number;
//   accountNo: string;
//   accountName: string;
//   bankName: string;
//   accountBalance: number;
// };

function MainPage() {
  // const token = "";

  // const [mainAccount, setMainAccount] = useState<Account | null>(null);
  // const [accountList, setAccountList] = useState<Array<Account> | null>(null);

  // useEffect(() => {
  //   const getAccountInfo = async () => {
  //     const response = await client(token).get("/api/accounts");
  //     setMainAccount(response.data.mainAccount);
  //     setAccountList(response.data.accountList);
  //   };

  //   getAccountInfo();
  // });

  return (
    <div className="px-6 py-20">
      <img src={logo} alt="로고" className="h-5 mb-3" />
      <div className="bg-primary-light h-56 rounded-2xl py-5 px-6 flex flex-col justify-between">
        <div>
          <h4 className="text-lg font-bold">Trabean 통장</h4>
          <span className="text-sm">1002-555-139750</span>
        </div>
        <div className="text-3xl font-bold">1,632,500 원</div>
        <button type="button" className="btn-lg">
          이체하기
        </button>
      </div>
      <div className="border-[1.5px] border-primary border-solid min-h-64 mt-5 rounded-xl p-4 flex flex-col justify-between">
        <Account />
        <div className="border-[1px] border-solid border-primary w-full" />
        <Account />
        <div className="border-[1px] border-solid border-primary w-full" />
        <Account />
        <div className="border-[1px] border-solid border-primary w-full" />
        <Account />
      </div>
      <div className="mt-5 border-[1.5px] border-primary-light border-solid rounded-lg flex justify-center items-center h-11">
        <img src={plusIcon} alt="통장추가버튼" className="h-5" />
      </div>
    </div>
  );
}

export default MainPage;