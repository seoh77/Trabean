import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import TopBar from "../../../components/TopBar";
import {
  DomesticTravelAccountDetailData,
  DomesticTravelAccountTransaction,
} from "../type/type";
import client from "../../../client";
import Loading from "../component/Loading";

const DomesticTravelAccountDetailPage: React.FC = () => {
  const { parentAccountId } = useParams();

  const [loading, setLoading] = useState(true);

  // 여행통장 상세조회 상태관리
  const [domesticTravelAccountDetailData, setDomesticTravelAccountDetailData] =
    useState<DomesticTravelAccountDetailData>();

  const handleTransferBalance = () => {
    console.log("이체 하기 누름!!!!!!");
  };

  // Account 서버 한화 여행통장 상세 조회 API fetch 요청
  useEffect(() => {
    const getTravelAccountData = async () => {
      const response = await client().get(
        `/api/accounts/travel/domestic/${parentAccountId}`,
      );
      setDomesticTravelAccountDetailData(response.data);
      console.log(response.data);
      setLoading(false);
    };
    if (parentAccountId) {
      getTravelAccountData();
    }
  }, [parentAccountId]);

  const getBalanceColor = (transactionType: string) => {
    if (transactionType === "1") {
      return "green";
    }
    if (transactionType === "2") {
      return "red";
    }
    return "black";
  };

  // 날짜 형식을 변환하는 함수
  const formatDate = (dateString: string): string => {
    const year = dateString.substring(0, 4); // 연도 추출
    const month = dateString.substring(4, 6); // 월 추출
    const day = dateString.substring(6, 8); // 일 추출
    return `${year}.${month}.${day}`; // 형식 변환
  };

  // 거래 내역을 날짜별로 그룹화하는 함수
  const groupByDate = (
    transactions: DomesticTravelAccountTransaction[],
  ): { [date: string]: DomesticTravelAccountTransaction[] } =>
    transactions.reduce(
      (acc, transaction) => {
        const date = transaction.transactionDate;
        if (!acc[date]) {
          acc[date] = [];
        }
        acc[date].push(transaction);
        return acc;
      },
      {} as { [date: string]: DomesticTravelAccountTransaction[] },
    );

  // 로딩 중이면 로딩 스피너 표시
  if (loading) {
    return <Loading />;
  }

  // 날짜별로 그룹화된 거래 내역을 렌더링하는 함수
  const renderGroupedTransactions = () => {
    if (!domesticTravelAccountDetailData?.transactionList) return null;

    const groupedTransactions = groupByDate(
      domesticTravelAccountDetailData.transactionList,
    );

    return Object.keys(groupedTransactions).map((date) => (
      <div key={date}>
        {/* 날짜 헤더 (변환된 날짜 표시) */}
        <div className="text-sm font-bold bg-zinc-100 my-2">
          {formatDate(date)}
        </div>
        {groupedTransactions[date].map((transaction, index) => (
          <div
            // eslint-disable-next-line react/no-array-index-key
            key={index}
            className="flex justify-between py-4 border-b border-gray-300"
          >
            <div>
              <div className="font-bold">{transaction.transactionSummary}</div>
              {transaction.transactionType === "1" ? (
                <div className="text-xs">입금</div>
              ) : (
                <div className="text-xs">출금</div>
              )}
            </div>
            <div className="text-right">
              <div
                style={{
                  color: getBalanceColor(transaction.transactionType),
                  fontWeight: "bold",
                }}
              >
                {transaction.transactionBalance > 0
                  ? `+₩${transaction.transactionBalance.toLocaleString()}`
                  : `-₩${Math.abs(transaction.transactionBalance).toLocaleString()}`}
              </div>
              <div className="text-xs">
                ₩{transaction.transactionAfterBalance.toLocaleString()}
              </div>
            </div>
          </div>
        ))}
      </div>
    ));
  };

  return (
    <div className="h-full relative">
      {/* 네비게이션 바 */}
      <div className="pt-16">
        <TopBar page="계좌 상세 조회" isWhite isLogo={false} />
      </div>

      {/* 한화 여행통장 정보 */}
      <div className="px-4 py-2">
        <div className="text-center">
          <div className="font-bold text-lg m-2">
            {domesticTravelAccountDetailData?.accountName}
          </div>
          <div className="font-bold m-2">
            ₩{domesticTravelAccountDetailData?.accountBalance.toLocaleString()}
          </div>
        </div>
        <div>
          <button
            type="button"
            onClick={handleTransferBalance}
            className="btn-md w-1/2 mx-auto block"
          >
            이체 하기
          </button>
        </div>
      </div>

      <div className="py-4">
        <hr />
      </div>

      {/* 거래 내역 */}
      <div>{renderGroupedTransactions()}</div>
    </div>
  );
};

export default DomesticTravelAccountDetailPage;
