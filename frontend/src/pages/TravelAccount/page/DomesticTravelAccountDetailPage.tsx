import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import TopBar from "../../../components/TopBar";
import {
  DomesticTravelAccountDetailData,
  TravelAccountData,
} from "../type/type";
import client from "../../../client";
import Loading from "../component/Loading";

const DomesticTravelAccountDetailPage: React.FC = () => {
  const { parentAccountId } = useParams();

  const [loading1, setLoading1] = useState(true);
  const [loading2, setLoading2] = useState(true);

  // 여행통장 상태관리
  const [travelAccountData, setTravelAccountData] =
    useState<TravelAccountData>();

  // 여행통장 멤버 상태관리
  const [domesticTravelAccountDetailData, setDomesticTravelAccountDetailData] =
    useState<DomesticTravelAccountDetailData>();

  // Travel 서버 여행통장 조회 API fetch 요청
  useEffect(() => {
    const getTravelAccountData = async () => {
      const response = await client().get(`/api/travel/${parentAccountId}`);
      setTravelAccountData(response.data);
      setLoading1(false);
    };

    if (parentAccountId) {
      getTravelAccountData();
    }
  }, [parentAccountId]);

  // Account 서버 한화 여행통장 멤버 목록 조회 API fetch 요청
  useEffect(() => {
    const getTravelAccountData = async () => {
      const response = await client().get(
        `/api/accounts/travel/domestic/${parentAccountId}/members`,
      );
      setDomesticTravelAccountDetailData(response.data);
      setLoading2(false);
    };
    if (parentAccountId) {
      getTravelAccountData();
    }
  }, [parentAccountId]);

  const getBalanceColor = (transactionType: string) => {
    if (transactionType === "1") {
      return "blue";
    }
    if (transactionType === "2") {
      return "red";
    }
    return "black";
  };

  // 로딩 중이면 로딩 스피너 표시
  if (loading1 || loading2) {
    return <Loading />;
  }

  return (
    <div>
      {/* 네비게이션 바 */}
      <div className="px-4 py-2">
        <TopBar page="계좌 상세 조회" isWhite isLogo={false} />
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
