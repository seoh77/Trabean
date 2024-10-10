import React, { useRef, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";

import QrScanner from "qr-scanner";
import TopBar from "../../components/TopBar";
import client from "../../client";

const PaymentPage: React.FC = () => {
  const videoRef = useRef<HTMLVideoElement | null>(null);
  const navigate = useNavigate();

  // 계좌번호 가져오기
  const location = useLocation();
  const accountId = location.pathname.split("/")[3];

  const paymentRoleValidate = async () => {
    console.log("권한검증API");
    if (!accountId) {
      return;
    }
    try {
      const response = await client().get(
        `/api/accounts/travel/domestic/${accountId}/userRole`,
      );
      if (response.data.userRole === "NONE_PAYER") {
        navigate(-1);
      }
    } catch (error) {
      console.error(error);
    }
  };

  useEffect(() => {
    console.log("useEffect실행");
    paymentRoleValidate();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [accountId]);

  const handleScan = (result: QrScanner.ScanResult) => {
    try {
      const parsedData = JSON.parse(result.data);
      console.log(parsedData);

      // 스캔된 URL로 리다이렉트
      if (parsedData && parsedData.url) {
        // 현재 경로에 QR 코드에서 나온 경로를 추가하여 상대 경로로 이동
        const newUrl = `${window.location.origin}${window.location.pathname}${parsedData.url}`;
        window.location.href = newUrl;
      }
    } catch (error) {
      console.error("Error parsing QR code data:", error);

      // URL 형태의 QR 코드일 경우 바로 리다이렉트
      if (result.data.startsWith("http")) {
        window.location.href = result.data;
      } else {
        // QR 코드 데이터가 URL이 아니면 상대 경로처럼 처리
        const newUrl = `${window.location.origin}${window.location.pathname}${result.data}`;
        window.location.href = newUrl;
      }
    }
  };

  // QrOptions 정의
  const QrOptions = {
    highlightScanRegion: true, // 스캔 영역 하이라이트 설정
    highlightCodeOutline: true, // QR 코드 윤곽 하이라이트 설정
  };

  useEffect(() => {
    const videoElem = videoRef.current;
    if (videoElem) {
      const qrScanner = new QrScanner(
        videoElem,
        (result) => handleScan(result),
        QrOptions,
      );
      qrScanner.start();

      return () => qrScanner.destroy();
    }
    return undefined;
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <div className="flex flex-col items-center h-dvh justify-center">
      <TopBar isWhite isLogo={false} page="QR 결제" />
      <div
        style={{
          width: "250px",
          height: "250px",
          border: "3px solid #6FA760",
          borderRadius: "15px",
          overflow: "hidden", // 비디오가 컨테이너 밖으로 나가는 것을 방지
        }}
      >
        <video
          ref={videoRef}
          style={{
            width: "100%",
            height: "100%",
            objectFit: "cover", // 화면이 꽉 차도록 비율 유지
            objectPosition: "center", // 중앙에 위치하도록 설정
            aspectRatio: "1 / 1", // 정사각형 비율을 유지
          }}
          autoPlay
        >
          <track kind="captions" src="" label="No captions needed" />
        </video>
      </div>
      <p className="mt-[20px] font-semibold">
        카메라 중앙에 QR 코드를 인식해주세요 !
      </p>
    </div>
  );
};

export default PaymentPage;
