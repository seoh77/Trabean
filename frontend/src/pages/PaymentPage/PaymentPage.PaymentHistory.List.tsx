import React from "react";
import client from "../../client";

const List: React.FC = () => {
  const fetchPaymentList = () => {
    const params = {
      startdate: startDate ? formatDate(startDate) : null,
      enddate: endDate ? formatDate(endDate) : null,
    };
    const response = await client(token || "").get(`/api/payments/1/chart`, {
      params,
    });
    console.log(response.data);
    const price = response.data.totalAmount;
    const formattedPrice = formatNumberWithCommas(price);
    handleTotalAmount(formattedPrice);
    setCartInfo(response.data.category);
    console.log(chartInfo);
  };
  return (
    <div className="w-full bg-white">
      <h2>Payment Page</h2>
    </div>
  );
};

export default List;
