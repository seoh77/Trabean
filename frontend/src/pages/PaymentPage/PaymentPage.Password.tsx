import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import TopBar from "../../components/TopBar";
import FailModal from "./PaymentPage.Password.FailModal";
import client from "../../client";
import useAuthStore from "../../store/useAuthStore";

const Password: React.FC = () => {
  const { merchantId, merchantName, currency, amount } = useParams();
  const [isFail, setIsFail] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [krwAmount, setKrwAmount] = useState<number | null>(null);
  const [foreignAmount, setForeignAmount] = useState<number | null>(null);

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

  const updatePaymentInfo = async () => {
    const accountId = useAuthStore.getState().paymentAccountId;
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
      await client().post(`/api/payments/info`, requestBody);
      setIsFail(false);
      setErrorMessage(null);
    } catch (error) {
      setIsFail(true);
      if (error instanceof Error) {
        setErrorMessage(error.message || "알 수 없는 에러가 발생했습니다.");
      }
    }
  };

  useEffect(() => {
    updatePaymentInfo();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  console.log(
    `merchantName: ${merchantName}, currency: ${currency}, amount: ${amount}`,
  );

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
      {isFail && <FailModal message={errorMessage} handleModal={handleModal} />}
      <TopBar isLogo={false} isWhite page="QR 결제" />
      <p className="text-lg pt-[100px]">{merchantName}</p>
      <h1 className="text-3xl font-semibold">
        {currency && currencyNameMap[currency]} {amount}
      </h1>
      <p className="text-lg mt-[40px]">계좌 비밀번호를 입력하세요.</p>
      <button type="button" className="btn-lg">
        결제
      </button>
    </div>
  );
};

export default Password;
