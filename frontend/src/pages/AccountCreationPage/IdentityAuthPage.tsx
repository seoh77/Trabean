import React, { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
// import NavBar from "./NavBar";
import TopBar from "../../components/TopBar";
import NextStepButton from "./NextStepButton";

const IdentityAuthPage: React.FC = () => {
  const [name, setName] = useState("");
  const [gender, setGender] = useState<"남성" | "여성" | "">("");
  const [telecom, setTelecom] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [birthdate, setBirthdate] = useState("");
  const [idLastNumber, setIdLastNumber] = useState("");
  const [isVerified, setIsVerified] = useState(false);
  const idLastNumberRef = useRef<HTMLInputElement>(null);

  const navigate = useNavigate();

  // 인증 상태 확인
  useEffect(() => {
    const storedIsVerified = sessionStorage.getItem("isVerified");
    if (!storedIsVerified || storedIsVerified !== "true") {
      // 인증되지 않았으면 접근을 차단하고 리다이렉트
      navigate("/creation");
    }
  }, [navigate]);

  // 생년월일 입력 핸들러
  const handleBirthdateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const input = e.target.value;

    // 숫자만 허용하고 6자리까지만 허용
    if (/^\d*$/.test(input) && input.length <= 6) {
      setBirthdate(input);

      // 6자리가 되었을 때 자동으로 다음 필드로 포커스 이동
      if (input.length === 6 && idLastNumberRef.current) {
        idLastNumberRef.current.focus();
      }
    }
  };

  // 입력 필드가 모두 채워졌는지 확인
  const isFormComplete = () =>
    name.length > 0 &&
    gender.length > 0 &&
    telecom.length > 0 &&
    phoneNumber.length === 13; // 휴대폰 번호 형식: 010-0000-0000

  // "다음 단계" 버튼 활성화 여부 확인
  const isNextButtonEnabled = () =>
    isVerified &&
    isFormComplete() &&
    birthdate.length > 0 &&
    idLastNumber.length === 1;

  // 인증 요청 처리
  const handleVerifyRequest = () => {
    if (isFormComplete()) {
      // 전화번호 인증 요청 로직
      setIsVerified(true); // 인증이 완료되면 인증 상태 업데이트
    }
  };

  const oninputPhone = (value: string) =>
    value
      .replace(/[^0-9]/g, "") // 숫자만 남기고 제거
      .replace(
        /(^02.{0}|^01.{1}|[0-9]{3,4})([0-9]{3,4})([0-9]{4})/g,
        "$1-$2-$3",
      );

  const handlePhoneChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const target = e.target as HTMLInputElement; // HTMLInputElement로 단언
    const formattedPhoneNumber = oninputPhone(target.value); // 입력값 포맷팅
    setPhoneNumber(formattedPhoneNumber); // 상태 업데이트
  };

  const handleIdentityAuth = () => {
    // 인증 완료버튼 클릭시 호출
    sessionStorage.setItem("isIdentityAuth", "true");
    navigate(`/creation/setup`);
  };

  return (
    <div>
      <TopBar isLogo={false} page="통장 개설" isWhite />
      <div className="px-6 py-20 bg-white">
        <div className="text-left">
          <h1 className="text-md mb-4">
            통장 개설을 위해 <span className="font-bold">본인 인증</span>을
            진행해주세요.
          </h1>
          <p className="mb-2 text-lg font-bold">
            {!name || !gender ? "개인정보를 입력해주세요" : null}
            {name && gender && !isVerified
              ? "휴대폰 번호를 입력해주세요"
              : null}
            {isVerified ? "생년월일을 입력해주세요" : null}
          </p>
          <p className="text-xs text-gray-500 mb-12">
            통신사에 등록된 정보와 같은지 확인해주세요. <br />
            띄어쓰기 없이 입력해야 해요.
          </p>
        </div>

        <div className="mb-6">
          <div className="flex items-center space-x-2 mb-6">
            {/* 이름 입력 필드 */}
            <div className="flex-1">
              <label htmlFor="name" className="block mb-2 text-sm font-bold">
                이름
              </label>
              <input
                id="name"
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                disabled={isVerified}
                placeholder="이름 입력"
                className="border rounded-md p-2 w-full h-10" // 높이를 맞추기 위해 h-12 추가
              />
            </div>

            {/* 성별 선택 버튼 */}
            <div className="flex items-center space-x-1 mt-6">
              {" "}
              {/* 성별 버튼 상단 마진으로 이름 라벨과 정렬 */}
              <button
                type="button"
                className={`px-4 py-2 rounded-3xl h-10 ${
                  gender === "남성" ? "bg-primary text-white" : "bg-gray-200"
                }`}
                onClick={() => setGender("남성")}
                disabled={isVerified}
              >
                남성
              </button>
              <button
                type="button"
                className={`px-4 py-2 rounded-3xl h-10 ${
                  gender === "여성" ? "bg-primary text-white" : "bg-gray-200"
                }`}
                onClick={() => setGender("여성")}
                disabled={isVerified}
              >
                여성
              </button>
            </div>
          </div>

          {/* 휴대폰 번호 */}
          {name &&
            gender && ( // 이름과 성별이 입력되었고, 인증이 완료되지 않은 경우에만 표시
              <div className="mb-6">
                <label
                  htmlFor="phoneNumber"
                  className="mb-2 block text-sm font-bold"
                >
                  휴대폰 번호
                </label>
                <div className="flex items-center space-x-2">
                  <select
                    id="telecom"
                    value={telecom}
                    onChange={(e) => setTelecom(e.target.value)}
                    disabled={isVerified}
                    className="border rounded-md px-1 py-2 w-20 h-10"
                  >
                    <option value="">통신사</option>
                    <option value="SKT">SKT</option>
                    <option value="KT">KT</option>
                    <option value="LG U+">LG U+</option>
                  </select>

                  <input
                    id="phoneNumber"
                    type="text"
                    value={phoneNumber}
                    onChange={handlePhoneChange}
                    disabled={isVerified}
                    placeholder="010-0000-0000"
                    className="border rounded-md px-1 py-2 w-full h-10"
                  />

                  <button
                    type="button"
                    className={`px-1 py-2 rounded-3xl w-40 ${
                      isFormComplete() ? "bg-primary text-white" : "bg-gray-200"
                    }`}
                    onClick={handleVerifyRequest}
                    disabled={!isFormComplete() || isVerified}
                  >
                    {isVerified ? "인증완료" : "인증요청"}
                  </button>
                </div>
              </div>
            )}

          {/* 생년월일 및 주민번호 뒷자리 */}
          {isVerified && ( // 인증이 완료된 경우에만 렌더링
            <div className="mb-10">
              <label
                htmlFor="birthdate"
                className="block mb-2 text-sm font-bold"
              >
                생년월일 + 주민번호 앞자리
              </label>

              <div className="flex items-center space-x-2 pl-0">
                <input
                  type="text"
                  value={birthdate}
                  onChange={handleBirthdateChange}
                  placeholder="YYMMDD"
                  className="border rounded-md p-2 w-full"
                />
                <span className="text-2xl text-gray-500">-</span>
                <input
                  ref={idLastNumberRef}
                  type="text"
                  value={idLastNumber}
                  onChange={(e) => setIdLastNumber(e.target.value)}
                  placeholder=""
                  className="border rounded-md p-2 w-1/4"
                />
                <span className="text-2xl">●●●●●●</span>
              </div>
            </div>
          )}
        </div>

        {/* 다음 단계 버튼 */}
        {isVerified && (
          <div className="flex justify-center">
            <NextStepButton
              isEnabled={isNextButtonEnabled()}
              onClick={handleIdentityAuth}
              text="다음 단계"
            />
          </div>
        )}
      </div>
    </div>
  );
};

export default IdentityAuthPage;
