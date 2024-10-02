import React, { useState, useEffect } from "react";
import client from "../../client";
import { formatNumberWithCommas } from "../../utils/formatNumber";

interface ListProps {
  token: string | null;
  startDate: string | null;
  endDate: string | null;
  formatDate: (date: string) => string;
}

const List: React.FC<ListProps> = ({
  startDate,
  endDate,
  token,
  formatDate,
}) => {
  const [page, setPage] = useState<number | null>(null);
  const [totalPage, setTotalPage] = useState<number | null>(null);

  const fetchPaymentList = async () => {
    const params = {
      startdate: startDate ? formatDate(startDate) : null,
      enddate: endDate ? formatDate(endDate) : null,
      page: page || null,
    };
    const response = await client(token || "").get(`/api/payments/1/`, {
      params,
    });
    console.log(response.data);
    setPage(response.data.pagination.currentPage);
    setTotalPage(response.data.pagination.totalPages);
  };

  useEffect(() => {
    fetchPaymentList();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [token]);

  return (
    <div className="w-full bg-white">
      <h2>Payment Page</h2>
      {totalPage}
    </div>
  );
};

export default List;
