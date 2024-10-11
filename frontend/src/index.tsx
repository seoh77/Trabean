import React, { useState, useEffect } from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import "./index.css";
import App from "./App";

const root = ReactDOM.createRoot(
  document.getElementById("root") as HTMLElement,
);
root.render(
  <BrowserRouter>
    <App />
  </BrowserRouter>,
);

// 서비스 워커 등록
if ("serviceWorker" in navigator) {
  window.addEventListener("load", () => {
    navigator.serviceWorker
      .register("/serviceWorker.js", { type: "module" })
      .then((registration) => {
        console.log(
          "Service Worker registered with scope:",
          registration.scope,
        );
      })
      .catch((error) => {
        console.log("Service Worker registration failed:", error);
      });
  });
}

// PWA 설치 요청 관련 로직 추가
const InstallPrompt = () => {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const [deferredPrompt, setDeferredPrompt] = useState<any>(null);

  useEffect(() => {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const handleBeforeInstallPrompt = (event: any) => {
      event.preventDefault(); // 자동으로 설치 창이 뜨는 것을 방지
      setDeferredPrompt(event); // 나중에 설치할 수 있도록 이벤트 저장
    };

    window.addEventListener("beforeinstallprompt", handleBeforeInstallPrompt);

    return () => {
      window.removeEventListener(
        "beforeinstallprompt",
        handleBeforeInstallPrompt,
      );
    };
  }, []);

  // 컴포넌트가 렌더링될 때 설치 여부를 바로 묻는 함수
  useEffect(() => {
    const handleInstallPrompt = async () => {
      if (deferredPrompt) {
        const installConfirmed = window.confirm("이 앱을 설치하시겠습니까?");
        if (installConfirmed) {
          deferredPrompt.prompt(); // 설치 요청 표시
          const { outcome } = await deferredPrompt.userChoice;
          if (outcome === "accepted") {
            alert("사용자가 PWA 설치를 수락했습니다.");
          } else {
            alert("사용자가 PWA 설치를 거부했습니다.");
          }
        } else {
          alert("사용자가 PWA 설치를 취소했습니다.");
        }
        setDeferredPrompt(null); // 설치 요청 후 초기화
      }
    };

    handleInstallPrompt();
  }, [deferredPrompt]);

  return null; // 버튼이 필요 없으므로 아무것도 렌더링하지 않음
};

// InstallPrompt 컴포넌트 렌더링
const installRoot = document.createElement("div");
document.body.appendChild(installRoot);

ReactDOM.createRoot(installRoot).render(<InstallPrompt />);
