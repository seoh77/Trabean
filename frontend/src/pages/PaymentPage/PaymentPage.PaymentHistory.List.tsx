import React, { useState, useEffect, useCallback } from "react";
import client from "../../client";
import { formatNumberWithCommas } from "../../utils/formatNumber";

interface ListProps {
  token: string | null;
  startDate: string | null;
  endDate: string | null;
  formatDate: (date: string) => string;
}

interface PaymentItem {
  payId: number;
  merchantName: string;
  paymentDate: string;
  krwAmount: number | null;
  foreignAmount: number | null;
  userName: string | null;
  category: string;
}

const List: React.FC<ListProps> = ({
  startDate,
  endDate,
  token,
  formatDate,
}) => {
  const [payments, setPayments] = useState<PaymentItem[]>([]);
  const [page, setPage] = useState<number>(1);
  const [totalPage, setTotalPage] = useState<number | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const fetchPaymentList = useCallback(
    async (reset = false) => {
      if (isLoading || (totalPage !== null && page > totalPage)) return;

      try {
        setIsLoading(true);
        const params = {
          startdate: startDate ? formatDate(startDate) : null,
          enddate: endDate ? formatDate(endDate) : null,
          page: reset ? 1 : page,
        };
        const response = await client(token || "").get(`/api/payments/1`, {
          params,
        });
        console.log(response.data);

        if (reset) {
          setPayments(response.data.payments);
        } else {
          setPayments((prev) => [...prev, ...response.data.payments]);
        }

        setPage(response.data.pagination.currentPage + 1);
        setTotalPage(response.data.pagination.totalPages);
      } catch (error) {
        console.error("Error fetching payment list:", error);
      } finally {
        setIsLoading(false);
      }
    },
    [token, page, totalPage, startDate, endDate, formatDate, isLoading],
  );

  // 초기 렌더링 및 날짜 변경 시 데이터를 다시 불러오기 위한 useEffect 훅
  useEffect(() => {
    setPage(1);
    setTotalPage(null);
    setPayments([]);
    fetchPaymentList(true);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [token, startDate, endDate]);

  // 스크롤 이벤트를 감지하여 무한 스크롤 구현
  useEffect(() => {
    const handleScroll = () => {
      if (
        window.innerHeight + window.scrollY >=
        document.documentElement.scrollHeight - 100
      ) {
        fetchPaymentList(false);
      }
    };

    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, [fetchPaymentList]);

  return (
    <div className="w-full bg-white">
      {payments &&
        payments.length > 0 &&
        payments.map((payment) => (
          <div key={payment.payId} className="w-full flex flex-col px-[20px]">
            <p>
              {payment.foreignAmount && (
                <span>{formatNumberWithCommas(payment.foreignAmount)} </span>
              )}
              {payment.krwAmount && (
                <span>{formatNumberWithCommas(payment.krwAmount)} ₩</span>
              )}
            </p>
            <p>Date: {payment.paymentDate}</p>
            <p>결제한 사람: {payment.userName}</p>
          </div>
        ))}
      {payments && payments.length === 0 && !isLoading ? (
        <p className="text-center my-5 text-gray-500">결제 내역이 없습니다.</p>
      ) : null}
      {isLoading && <div className="text-center">Loading...</div>}
    </div>
  );
};

export default List;
