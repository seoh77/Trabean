import React from "react";
import { useNavigate, useLocation } from "react-router-dom";
import classNames from "classnames";

import Home from "../assets/home.png";
import PaymentHistory from "../assets/paymentHistory.png";
import Payment from "../assets/payment.png";
import ExchangeRate from "../assets/exchangeRate.png";

const BottomBar: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const containerClass = classNames(
    "flex flex-col items-center justify-center relative w-full hover:cursor-pointer",
  );
  const imgClass = classNames("w-[25px] absolute -bottom-1");
  const textClass = classNames("text-xs absolute -bottom-6");
  const isShowBottom = !location.pathname.startsWith("/payment/qr");

  return (
    <>
      {isShowBottom && (
        <div className="w-[360px] h-[60px] flex items-center fixed bottom-0 text-[#999999] shadow-2xl justify-around bg-white">
          <div
            onClick={() => {
              navigate("/");
            }}
            role="presentation"
            className={containerClass}
          >
            <img src={Home} alt="home" className={imgClass} />
            <p className={textClass}>홈</p>
          </div>
          <div
            onClick={() => {
              navigate("/payment/list");
            }}
            role="presentation"
            className={containerClass}
          >
            <img
              src={PaymentHistory}
              alt="PaymentHistory"
              className={imgClass}
            />
            <p className={textClass}>가계부</p>
          </div>
          <div
            onClick={() => {
              navigate("/exchange");
            }}
            role="presentation"
            className={containerClass}
          >
            <img src={ExchangeRate} alt="exchangeRate" className={imgClass} />
            <p className={textClass}>환율</p>
          </div>
          <div
            onClick={() => {
              navigate("/payment/qr");
            }}
            role="presentation"
            className={containerClass}
          >
            <img src={Payment} alt="Payment" className={imgClass} />
            <p className={textClass}>결제</p>
          </div>
        </div>
      )}
      {null}
    </>
  );
};

export default BottomBar;
