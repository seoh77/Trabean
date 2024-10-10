import { useEffect, useState } from "react";
import { Outlet, useNavigate } from "react-router-dom";
import useAuthStore from "../store/useAuthStore";

const ProtectedRoute: React.FC = () => {
  const [isLoading, setIsLoading] = useState(true);
  const { accessToken } = useAuthStore(); // 로그인 토큰 확인
  const navigate = useNavigate();

  useEffect(() => {
    const checkLogin = () => {
      setIsLoading(true);

      // 로그인되지 않았을 경우, 로그인 페이지로 리다이렉트
      if (!accessToken) {
        navigate("/login"); // 로그인 페이지로 리다이렉트
      } else {
        setIsLoading(false); // 로그인 상태일 때만 로딩 해제
      }
    };

    checkLogin();
  }, [accessToken, navigate]);

  if (isLoading) {
    return <div>Loading...</div>; // 로딩 중 화면을 표시
  }

  return <Outlet />; // 로그인되어 있으면 자식 컴포넌트를 렌더링
};

export default ProtectedRoute;
