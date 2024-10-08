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
      .register("./serviceWorker.js")
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
  const [deferredPrompt, setDeferredPrompt] = useState<any>(null);
  const [isInstallable, setIsInstallable] = useState(false);

  useEffect(() => {
    const handleBeforeInstallPrompt = (event: any) => {
      event.preventDefault(); // 자동으로 설치 창이 뜨는 것을 방지
      setDeferredPrompt(event); // 나중에 설치할 수 있도록 이벤트 저장
      setIsInstallable(true); // 설치 가능한 상태로 설정
    };

    window.addEventListener("beforeinstallprompt", handleBeforeInstallPrompt);

    return () => {
      window.removeEventListener(
        "beforeinstallprompt",
        handleBeforeInstallPrompt,
      );
    };
  }, []);

  const handleInstallClick = async () => {
    if (deferredPrompt) {
      deferredPrompt.prompt(); // 설치 요청 표시
      const { outcome } = await deferredPrompt.userChoice;
      if (outcome === "accepted") {
        console.log("사용자가 PWA 설치를 수락했습니다.");
      } else {
        console.log("사용자가 PWA 설치를 거부했습니다.");
      }
      setDeferredPrompt(null); // 설치 요청 후 초기화
      setIsInstallable(false); // 설치 버튼 숨김
    }
  };

  return (
    <>
      {isInstallable && (
        <button type="button" onClick={handleInstallClick}>
          이 앱을 설치하시겠습니까?
        </button>
      )}
      null
    </>
  );
};

// InstallPrompt 컴포넌트 렌더링
const installRoot = document.createElement("div");
document.body.appendChild(installRoot);

ReactDOM.createRoot(installRoot).render(<InstallPrompt />);
