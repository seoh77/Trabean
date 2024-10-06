import React, {
  createContext,
  useContext,
  useState,
  useMemo,
  useCallback,
  ReactNode,
} from "react";

// Context의 타입 정의
interface AccountTypeContext {
  accountType: "personal" | "travel" | null; // 개인/여행/초기 상태(null)
  setAccountType: (type: "personal" | "travel") => void; // 통장 유형 설정 함수
  resetAccountType: () => void; // 통장 유형 초기화 함수
}

// 초기값 설정
const AccountTypeContext = createContext<AccountTypeContext | undefined>(
  undefined,
);

// Context에 대한 쉬운 접근을 위한 Hook
export const useAccountType = () => {
  const context = useContext(AccountTypeContext);
  if (!context) {
    throw new Error(
      "AccountTypeProvider로 감싼 컴포넌트에서만 useAccountType 호출 가능",
    );
  }
  return context;
};

// Provider Component: 상태 관리 및 제공
export const AccountTypeProvider = ({ children }: { children: ReactNode }) => {
  const [accountType, setAccountType] = useState<"personal" | "travel" | null>(
    null,
  );

  // useCallback으로 resetAccountType 함수 메모이제이션
  // =>
  const resetAccountType = useCallback(() => {
    setAccountType(null);
  }, []);

  // useMemo로 value 객체 메모이제이션
  // => 특정 값이 바뀔 때만 재계산하여 객체를 재생성하여 불필요한 리렌더링 방지
  const value = useMemo(
    () => ({ accountType, setAccountType, resetAccountType }),
    [accountType, setAccountType, resetAccountType],
  );

  return (
    <AccountTypeContext.Provider value={value}>
      {children}
    </AccountTypeContext.Provider>
  );
};
