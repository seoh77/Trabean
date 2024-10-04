import React, {
  createContext,
  useContext,
  useState,
  ReactNode,
  useMemo,
} from "react";

// 계좌 유형 타입 정의
type AccountType = "personal" | "group" | null;

// 컨텍스트 생성
const AccountTypeContext = createContext<{
  accountType: AccountType;
  setAccountType: React.Dispatch<React.SetStateAction<AccountType>> | undefined;
}>({
  accountType: null, // 기본값 설정
  setAccountType: undefined, // 빈 함수 대신 undefined로 설정
});

// 컨텍스트 프로바이더 정의
export const AccountTypeProvider: React.FC<{ children: ReactNode }> = ({
  children,
}) => {
  const [accountType, setAccountType] = useState<AccountType>(null);

  // value 객체를 useMemo로 메모이제이션
  const value = useMemo(
    () => ({ accountType, setAccountType }),
    [accountType, setAccountType],
  );

  return (
    <AccountTypeContext.Provider value={value}>
      {children}
    </AccountTypeContext.Provider>
  );
};

// 커스텀 훅 정의
export const useAccountType = () => {
  const context = useContext(AccountTypeContext);
  if (!context) {
    throw new Error(
      "useAccountType must be used within an AccountTypeProvider",
    );
  }
  return context;
};
