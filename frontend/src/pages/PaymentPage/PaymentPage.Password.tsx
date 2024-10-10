import { useEffect, useState } from "react";
import { useParams, useNavigate, useLocation } from "react-router-dom";
import { isAxiosError } from "axios";

import TopBar from "../../components/TopBar";
import FailModal from "./PaymentPage.Password.FailModal";
import Keypad from "../AccountCreationPage/Keypad";
import client from "../../client";
import { formatNumberWithCommas } from "../../utils/formatNumber";

const Password: React.FC = () => {
  const { merchantId, merchantName, currency, amount } = useParams();
  const [isFail, setIsFail] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [krwAmount, setKrwAmount] = useState<number | null>(null);
  const [foreignAmount, setForeignAmount] = useState<number | null>(null);
  const [password, setPassword] = useState<string>("");
  const [payId, setPayId] = useState<number | null>(null);
  const [isRetry, setIsRetry] = useState<boolean>(false);
  const [transId, setTransId] = useState<string | null>(null);

  // 계좌번호 가져오기
  const location = useLocation();
  const accountId = location.pathname.split("/")[3];

  useEffect(() => {
    if (!amount || !currency) {
      return;
    }
    if (currency === "KRW") {
      setKrwAmount(parseInt(amount, 10));
    } else {
      setForeignAmount(parseInt(amount, 10));
    }
  }, [amount, currency]);

  const navigate = useNavigate();
  const validateInfo = () => {
    if (amount === undefined) {
      setErrorMessage("가격 정보를 불러오는 데 실패했습니다.");
      setIsFail(true);
      return;
    }
    if (currency === undefined) {
      setErrorMessage("통화 정보를 불러오는 데 실패했습니다.");
      setIsFail(true);
      return;
    }
    if (merchantId === undefined) {
      setErrorMessage("가맹점 정보를 불러오는 데 실패했습니다.");
      setIsFail(true);
      return;
    }
    if (currency === "KRW") {
      setKrwAmount(parseInt(amount, 10));
    } else {
      setForeignAmount(parseInt(amount, 10));
    }
  };

  const handleModal = () => {
    setIsFail(false);
    setErrorMessage(null);
  };

  // qr 읽어온 직후 정보 업데이트 api 호출
  const updatePaymentInfo = async () => {
    try {
      validateInfo();
      const requestBody: {
        accountId: string | null;
        merchantId?: string;
        krwAmount?: number;
        foreignAmount?: number;
      } = {
        accountId,
        merchantId,
      };
      if (krwAmount !== null) {
        requestBody.krwAmount = krwAmount;
      } else if (foreignAmount !== null) {
        requestBody.foreignAmount = foreignAmount;
      }
      const response = await client().post(`/api/payments/info`, requestBody);
      setIsFail(false);
      setErrorMessage(null);
      setErrorMessage(response.data);
      console.log(response.data);
      setPayId(response.data.data.payId);
    } catch (error) {
      setIsFail(true);
      if (error instanceof Error) {
        setErrorMessage(error.message || "알 수 없는 에러가 발생했습니다.");
        setIsFail(true);
      }
    }
  };

  useEffect(() => {
    if (
      !merchantId ||
      !currency ||
      !accountId ||
      (!krwAmount && !foreignAmount) ||
      payId
    ) {
      return;
    }

    updatePaymentInfo();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [merchantId, currency, krwAmount, foreignAmount, accountId]);

  // 결제 요청 api 호출
  const requestPayment = async (transactionId: string) => {
    try {
      // 요청 바디 생성
      const requestBody = {
        transactionId,
        payId,
        merchantId,
        krwAmount: krwAmount ?? null,
        foreignAmount: foreignAmount ?? null,
      };
      // API 요청 전송
      const response = await client().post(
        `/api/payments/${accountId}`,
        requestBody,
      );
      console.log(response.data);
      // 성공시 성공 url 로 이동
      if (response.data.status === "SUCCESS") {
        navigate(`/payment/qr/success/${payId}`);
      }
    } catch (error) {
      if (payId == null) {
        // payId가 null일 때
        setErrorMessage("결제 ID를 가져오는 데 실패했습니다.");
        setIsFail(true);
        return;
      }

      if (isAxiosError(error)) {
        // 외화 계좌 없을 때
        if (error.response?.data.message === "FOREIGN_ACCOUNT_NOT_FOUND") {
          setErrorMessage(
            `해당 통화의 계좌를 찾을 수 없습니다. 한화로 ${formatNumberWithCommas(error.response?.data.krwPrice)} 원을 결제하시겠습니까?`,
          );
          setTransId(transactionId);
          setKrwAmount(error.response?.data.krwPrice);
          setIsFail(true);
          setIsRetry(true);
          return;
        }
        // 외화 계좌 잔액 부족
        if (error.response?.data.message === "FOREIGN_ACCOUNT_BALANCE_ERROR") {
          setErrorMessage(
            `해당 통화 잔액이 부족합니다. 한화로 ${formatNumberWithCommas(error.response?.data.krwPrice)} 원을 결제하시겠습니까?`,
          );
          setTransId(transactionId);
          setKrwAmount(error.response?.data.krwPrice);
          setIsFail(true);
          setIsRetry(true);
          return;
        }

        // 에러 메시지 설정
        setErrorMessage(error.response?.data.message || "알 수 없는 에러 발생");

        setIsFail(true);
      }
    }
  };

  const changePasswordInput = (newPassword: string) => {
    setPassword(newPassword);
  };

  // 비밀번호 입력 완료
  const submitPassword = async () => {
    try {
      // 요청 바디 생성
      const requestBody = {
        payId,
        accountId,
        password,
      };

      // API 요청 전송
      const response = await client().post(
        `/api/payments/validate`,
        requestBody,
      );
      console.log(response.data);

      // 발급된 transactionId 로 결제요청
      requestPayment(response.data.transactionId);
    } catch (error) {
      if (payId == null) {
        // payId가 null일 때
        setErrorMessage("결제 ID를 가져오는 데 실패했습니다.");
        setIsFail(true);
        setPassword("");
        return;
      }
      if (isAxiosError(error)) {
        // 에러 메시지 설정
        setErrorMessage(error.response?.data.message || "알 수 없는 에러 발생");
        setIsFail(true);
      }
      setPassword("");
    }
  };

  // 재결제 로직 작성
  const retryPaymentRequest = async () => {
    try {
      // 요청 바디 생성
      const requestBody = {
        transactionId: transId,
        payId,
        merchantId,
        krwAmount: krwAmount ?? null,
      };
      // API 요청 전송
      const response = await client().post(
        `/api/payments/retry/${accountId}`,
        requestBody,
      );
      console.log(response.data);
      // 성공시 성공 url 로 이동
      if (response.data.status === "SUCCESS") {
        navigate(`/payment/qr/success/${payId}`);
      }
    } catch (error) {
      if (payId == null) {
        // payId가 null일 때
        setErrorMessage("결제 ID를 가져오는 데 실패했습니다.");
        setIsFail(true);
        return;
      }
      if (isAxiosError(error)) {
        // 에러 메시지 설정
        setErrorMessage(error.response?.data.message || "알 수 없는 에러 발생");
        setIsFail(true);
      }
      setPassword("");
    }
  };

  // 화폐 이름 매핑
  const currencyNameMap: { [key: string]: string } = {
    CAD: "$ ",
    CHF: "₣ ",
    CNY: "¥ ",
    EUR: "€ ",
    GBP: "£ ",
    JPY: "¥ ",
    USD: "$ ",
    KRW: "₩ ",
  };

  return (
    <div className="flex w-full h-dvh flex-col items-center relative">
      {isFail && (
        <FailModal
          message={errorMessage}
          handleModal={handleModal}
          isRetry={isRetry}
          retryPaymentRequest={retryPaymentRequest}
        />
      )}
      <TopBar isLogo={false} isWhite page="QR 결제" />

      <p className="text-lg pt-[100px]">{merchantName}</p>
      <h1 className="text-3xl font-semibold">
        {currency && currencyNameMap[currency]} {amount}
      </h1>
      <p className="text-lg mt-[40px] mb-5">계좌 비밀번호를 입력하세요.</p>
      <Keypad
        password={password}
        onChange={changePasswordInput}
        onComplete={submitPassword}
      />
    </div>
  );
};

export default Password;
