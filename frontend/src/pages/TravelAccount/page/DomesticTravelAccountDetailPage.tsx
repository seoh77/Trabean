import { useEffect, useState } from "react";
import {
  TravelAccountData,
  DomesticTravelAccountDetailData,
} from "../type/type";
import TravelAccountDataDummy from "../dummy/TravelAccountDataDummy";
import DomesticTravelAccountDetailDataDummy from "../dummy/DomesticTravelAccountDetailDataDummy";
import NavBar from "../component/NavBar";

// import { useParams } from "react-router-dom";

const DomesticTravelAccountDetailPage = () => {
  // const { accountId } = useParams<{ accountId: string }>();
  const [travelAccountData, setTravelAccountData] =
    useState<TravelAccountData>();
  const [domesticTravelAccountDetailData, setDomesticTravelAccountDetailData] =
    useState<DomesticTravelAccountDetailData>();

  // 여행통장 계좌 정보를 받는 fetch 요청
  useEffect(() => {
    setTravelAccountData(TravelAccountDataDummy);
  }, []);

  // 여행통장 거래 내역 정보를 받는 fetch 요청
  useEffect(() => {
    setTravelAccountData(TravelAccountDataDummy);
    setDomesticTravelAccountDetailData(DomesticTravelAccountDetailDataDummy);
  }, []);

  const getBalanceColor = (transactionType: string) => {
    if (transactionType === "1") {
      return "blue";
    }
    if (transactionType === "2") {
      return "red";
    }
    return "black";
  };

  return (
    <div>
      {/* 네비게이션 바 */}
      <div className="px-4 py-2">
        <NavBar text="계좌 상세 조회" />
      </div>

      {/* 한화 여행통장 정보 */}
      <div>
        <div>{travelAccountData?.accountName}</div>
        <div>
          ₩{travelAccountData?.account[0].accountBalance.toLocaleString()}
        </div>
        <div>
          <button type="button" className="btn-md">
            이체 하기
          </button>
        </div>
      </div>

      {/* 거래 내역 */}
      <div>
        {domesticTravelAccountDetailData?.transactionList.map(
          (transaction, index) => (
            // eslint-disable-next-line react/no-array-index-key
            <div key={index}>
              <div>{transaction.transactionSummary}</div>
              <div>
                {transaction.transactionDate}-{transaction.transactionTime}
              </div>
              <div
                style={{ color: getBalanceColor(transaction.transactionType) }}
              >
                잔액: {transaction.transactionBalance}
              </div>
              <div>거래 후 잔액: {transaction.transactionAfterBalance}</div>
            </div>
          ),
        )}
      </div>
    </div>
  );
};

export default DomesticTravelAccountDetailPage;
