import React, { useState, useEffect } from "react";

import beanProfile from "../../assets/bean_profile.png";
import kingBeanProfile from "../../assets/bean_admin.png"; // kingbean 이미지 추가
import checkIcon from "../../assets/successIcon.png"; // 성공 아이콘
import warningIcon from "../../assets/importantIcon.png"; // 경고 아이콘
import { TravelAccountMember } from "../TravelAccount/type/type";

const ExchangeSplit: React.FC = () => {
  const [isVisible, setIsVisible] = useState(true); // State to control modal visibility
  const [step, setStep] = useState(1);
  const [dividedAmount, setDividedAmount] = useState<number>(0);
  const [selectedMembers, setSelectedMembers] = useState<TravelAccountMember[]>(
    [],
  ); // 선택된 멤버 목록
  const [errorModalVisible, setErrorModalVisible] = useState(false); // 오류 모달 상태

  // 하드코딩된 예시 데이터
  const exampleData = {
    totalAmount: 100000,
    totalNo: 4,
    withdrawalAccountId: "1234567890",
    withdrawalAccountNo: "356-0630-5770-33",
    depositAccountList: [
      {
        userId: 1,
        userName: "김철수",
        role: "admin", // admin 역할
        amount: 25000,
        mainAccountId: 12345,
        mainAccountNo: "123-456-789012",
      },
      {
        userId: 2,
        userName: "이영희",
        role: "member",
        amount: 25000,
        mainAccountId: null, // 메인 계좌가 설정되지 않음
        mainAccountNo: "",
      },
      {
        userId: 3,
        userName: "박수민",
        role: "member",
        amount: 25000,
        mainAccountId: 67890,
        mainAccountNo: "123-456-789013",
      },
      {
        userId: 4,
        userName: "최지원",
        role: "member",
        amount: 25000,
        mainAccountId: 54321,
        mainAccountNo: "123-456-789014",
      },
    ],
  };

  useEffect(() => {
    // 하드코딩된 데이터를 설정
    setDividedAmount(
      Math.floor(exampleData.totalAmount! / exampleData.totalNo!),
    );
  }, []);

  const toggleMemberSelection = (member: TravelAccountMember) => {
    setSelectedMembers((prevSelected) => {
      if (prevSelected.some((selected) => selected.userId === member.userId)) {
        return prevSelected.filter(
          (selected) => selected.userId !== member.userId,
        );
      }
      return [...prevSelected, member];
    });
  };

  const handleSplit = () => {
    const invalidMembers = selectedMembers.filter(
      (member) => member.mainAccountId === null,
    );

    if (invalidMembers.length > 0) {
      setErrorModalVisible(true); // 메인 계좌가 없는 멤버가 있으면 오류 모달을 띄움
    } else {
      setStep(2); // N빵 하시겠습니까? 모달로 이동
    }
  };

  const closeErrorModal = () => {
    setErrorModalVisible(false);
  };

  const handleCloseModal = () => {
    setIsVisible(false);
    setStep(1); // step을 초기 상태로 변경
    window.location.reload(); // 페이지 새로고침
  };

  const handleCloseModal1 = () => {
    // setIsVisible(false);
    setStep(1); // step을 초기 상태로 변경
    // window.location.reload(); // 페이지 새로고침
  };

  // "N빵 하시겠습니까?" 모달에서 예를 누르면 step 3로 이동
  const confirmNBbang = () => {
    setStep(3);
  };

  // "N빵 하시겠습니까?" 모달에서 아니오를 누르면 모달만 닫기
  const cancelNBbang = () => {
    setStep(1);
  };
  const handleCloseModalfirst = () => {
    setIsVisible(false);
    setStep(1); // step을 초기 상태로 변경
    window.location.reload(); // 페이지 새로고침
    cancelNBbang();
  };
  return (
    <div className="relative">
      {/* 배경 오버레이 */}
      {isVisible && (
        // eslint-disable-next-line jsx-a11y/click-events-have-key-events, jsx-a11y/no-static-element-interactions
        <div
          className="fixed inset-0 bg-gray-900 bg-opacity-50"
          onClick={handleCloseModal} // 오버레이 클릭 시 모달 닫기
          style={{ zIndex: 900 }} // 기본 모달의 z-index
        />
      )}

      {/* Modal Wrapper */}
      <div
        className={`fixed bottom-[60px] left-0 right-0 w-full max-w-[360px] mx-auto mt-4 ${
          isVisible ? "block" : "hidden"
        }`}
        style={{ zIndex: 901 }} // 기본 모달의 z-index
      >
        {/* Modal */}
        <div
          role="presentation"
          className="bg-white p-4 rounded-[40px] shadow-lg border border-gray-300 h-full flex flex-col items-center justify-center"
          onClick={(e) => e.stopPropagation()} // 모달 클릭 시 오버레이 닫히는 동작 방지
        >
          {/* step 1: N빵 목록 */}
          {step === 1 && (
            <>
              <h1 className="text-2xl font-bold text-center flex m-auto items-center text-black mb-4">
                N빵 나누기
              </h1>
              <div className="mb-2">
                <p className="text-gray-600">
                  출금 계좌: {exampleData.withdrawalAccountNo}
                </p>
              </div>

              {/* 스크롤 가능한 목록 */}
              <div className="flex-grow overflow-y-auto space-y-2 mb-4 max-h-[300px] w-full">
                {exampleData.depositAccountList?.map((member) => (
                  <div
                    key={member.userId}
                    className={`flex justify-between items-center p-2 bg-[#F9F9F9] rounded shadow ${
                      selectedMembers.some(
                        (selected) => selected.userId === member.userId,
                      )
                        ? "bg-green-100"
                        : ""
                    }`}
                    onClick={() => toggleMemberSelection(member)}
                    role="button"
                    tabIndex={0}
                    onKeyPress={() => toggleMemberSelection(member)}
                  >
                    <div className="flex items-center">
                      <span className="relative mr-3">
                        {/* role이 admin이면 kingbean 이미지로, 아니라면 일반 bean 이미지로 설정 */}
                        <img
                          src={
                            member.role === "admin"
                              ? kingBeanProfile
                              : beanProfile
                          }
                          alt="profile"
                          className="w-8 h-8 object-cover rounded-full"
                        />
                      </span>
                      <div className="flex flex-col">
                        <p className="text-sm text-gray-800">
                          {member.userName}
                        </p>
                        <p className="text-xs text-gray-500">
                          {member.mainAccountNo || "계좌번호 없음"}
                        </p>
                      </div>
                    </div>
                    <p className="font-bold text-sm text-[#2F8F4E]">
                      ₩ {dividedAmount.toLocaleString()}
                    </p>
                  </div>
                ))}
              </div>

              {/* 고정된 버튼 */}
              <div className="flex justify-between mt-4 w-full">
                <button
                  type="button"
                  onClick={handleCloseModalfirst}
                  className="btn-lg bg-gray-200 text-gray-600 hover:bg-gray-300 w-[115px]"
                >
                  취소
                </button>
                <button
                  type="button"
                  onClick={handleSplit}
                  className="btn-lg text-white hover:bg-green-600 w-[115px]"
                >
                  확인
                </button>
              </div>
            </>
          )}

          {/* step 2: N빵 확인 모달 */}
          {step === 2 && (
            <>
              <h1 className="text-xl font-bold text-center mb-4">
                진짜 N빵 하시나요?
              </h1>
              <div className="flex justify-between w-full mt-4">
                <button
                  type="button"
                  onClick={handleCloseModal1}
                  className="px-4 py-2 rounded bg-gray-200 text-gray-600 hover:bg-gray-300 w-[40%]"
                >
                  취소
                </button>
                <button
                  type="button"
                  onClick={confirmNBbang}
                  className="px-4 py-2 rounded bg-green-500 text-white hover:bg-green-600 w-[40%]"
                >
                  확인
                </button>
              </div>
            </>
          )}

          {/* step 3: N빵 성공 모달 */}
          {step === 3 && (
            <>
              <h1 className="text-xl font-bold text-center mb-4">
                N빵이 완료되었습니다!
              </h1>
              <img
                src={checkIcon}
                alt="success"
                className="w-16 h-16 mb-6" // 더 크게 조정
              />
              <button
                type="button"
                onClick={handleCloseModal}
                className="px-4 py-2 rounded bg-primary text-white w-full hover:bg-green-600"
              >
                닫기
              </button>
            </>
          )}

          {/* N빵 실패 모달 */}
          {errorModalVisible && (
            <>
              <h1 className="text-xl font-bold text-center mb-4">
                N빵에 실패하였습니다!
              </h1>
              <img
                src={warningIcon}
                alt="fail"
                className="w-16 h-16 mb-6" // 더 크게 조정
              />
              <p className="text-gray-800 text-center mb-4">
                선택된 멤버 중 메인 계좌가 설정되지 않은 사람이 있습니다. 설정
                후 다시 시도해주세요.
              </p>
              <button
                type="button"
                onClick={closeErrorModal}
                className="px-4 py-2 rounded bg-red-600 text-white w-full hover:bg-red-700"
              >
                확인
              </button>
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default ExchangeSplit;
