import React, { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import classNames from "classnames";

import { EventSourcePolyfill, NativeEventSource } from "event-source-polyfill";
import useAuthStore from "../store/useAuthStore";

import Home from "../assets/home.png";
import PaymentHistory from "../assets/paymentHistory.png";
import Payment from "../assets/payment.png";
import ExchangeRate from "../assets/exchangeRate.png";
import Bell from "../assets/Bell.png";

const BottomBar: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const token = useAuthStore.getState().accessToken;

  const [hasNotification, setHasNotification] = useState(false);

  useEffect(() => {
    if (!token) return;

    const EventSource = EventSourcePolyfill || NativeEventSource;

    const eventSource = new EventSource(
      `${process.env.REACT_APP_END_POINT}/api/notifications/status`,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
        withCredentials: true,
      },
    );

    eventSource.onmessage = (event: MessageEvent) => {
      const data = JSON.parse(event.data);
      if (data.newNotification) {
        setHasNotification(true);
      }
    };

    eventSource.onerror = (event: MessageEvent) => {
      console.error("Error occurred:", event);
      eventSource.close();
    };

    // eslint-disable-next-line consistent-return
    return () => {
      eventSource.close();
    };
  }, [token]);

  const containerClass = classNames(
    "flex flex-col items-center justify-center relative w-full hover:cursor-pointer",
  );
  const imgClass = classNames("w-[25px] absolute -bottom-1");
  const textClass = classNames("text-xs absolute -bottom-6");
  const isShowBottom =
    !location.pathname.startsWith("/payment/qr") &&
    !location.pathname.startsWith("/login") &&
    !location.pathname.startsWith("/join");

  return (
    <>
      {isShowBottom && (
        <>
          <div className="pt-[60px]" />
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
            <div
              onClick={() => {
                navigate("/notification");
              }}
              role="presentation"
              className={`${containerClass} relative`}
            >
              <img src={Bell} alt="Bell" className={imgClass} />
              <p className={textClass}>알림</p>
              {hasNotification && (
                <div className="w-2 h-2 rounded-full bg-red-500 z-20 absolute right-5 top-[-23px]" />
              )}
            </div>
          </div>
        </>
      )}
      {null}
    </>
  );
};

export default BottomBar;
