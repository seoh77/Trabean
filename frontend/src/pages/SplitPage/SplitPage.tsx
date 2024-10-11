import React, { useState, useEffect } from "react";

import beanProfile from "../../assets/bean_profile.png";
import kingBeanProfile from "../../assets/bean_admin.png"; // kingbean 이미지 추가
import checkIcon from "../../assets/successIcon.png"; // 성공 아이콘
import warningIcon from "../../assets/importantIcon.png"; // 경고 아이콘
import { TravelAccountMember } from "../TravelAccount/type/type";
import client from "../../client";

interface SplitProps {
  totalAmount: number;
  withdrawalAccountNo: string | undefined;
  withdrawalAccountId: string | undefined; // 출금계좌ID 추가
  depositAccountList: TravelAccountMember[] | undefined;
  onClose: () => void;
}

const ExchangeSplit: React.FC<SplitProps> = ({
  totalAmount,
  withdrawalAccountNo,
  withdrawalAccountId, // 출금계좌ID 추가
  depositAccountList,
  onClose,
}) => {
  const [isVisible, setIsVisible] = useState(true); // N빵 나누기 모달
  const [isConfirmModalVisible, setIsConfirmModalVisible] = useState(false); // N빵 확인 모달
  const [isSuccessModalVisible, setIsSuccessModalVisible] = useState(false); // N빵 성공 모달
  const [isErrorModalVisible, setIsErrorModalVisible] = useState(false); // 오류 모달

  const [dividedAmount, setDividedAmount] = useState<number>(0);
  const [selectedMembers, setSelectedMembers] = useState<TravelAccountMember[]>(
    [],
  ); // 선택된 멤버 목록

  useEffect(() => {
    // selectedMembers의 길이로 totalNo를 대체하고, 그에 따라 dividedAmount 계산
    const totalNo = selectedMembers.length;
    if (totalNo > 0) {
      setDividedAmount(Math.floor(totalAmount / totalNo));
    }
  }, [totalAmount, selectedMembers]);

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

  // N빵 진행 확인 버튼 클릭 시
  const handleSplit = async () => {
    const invalidMembers = selectedMembers.filter(
      (member) => member.mainAccountId === null,
    );
    console.log(invalidMembers.length);
    const result = invalidMembers.length;
    console.log(result);
    if (result) {
      setIsErrorModalVisible(true); // 메인 계좌가 없는 멤버가 있으면 오류 모달을 띄움
    } else {
      setIsConfirmModalVisible(true); // N빵 확인 모달 띄우기
    }
  };

  const handleSplit2 = async () => {
    try {
      // POST 요청을 위한 데이터 구조 생성
      const requestData = {
        totalAmount,
        totalNo: selectedMembers.length, // 선택된 멤버 수
        withdrawalAccountId,
        withdrawalAccountNo,
        depositAccountList: selectedMembers.map((member) => ({
          userId: member.userId,
          accountNumber: member.mainAccountNumber,
        })),
      };

      // API로 POST 요청 보내기
      const response = await client().post("/api/travel/split", requestData);

      if (response.status === 200) {
        setIsConfirmModalVisible(false);
        setIsSuccessModalVisible(true); // N빵 성공 모달 띄우기
      } else {
        setIsErrorModalVisible(true); // N빵 실패 모달 띄우기
      }
    } catch (error) {
      console.error("N빵 처리 중 오류 발생", error);
      setIsErrorModalVisible(true); // 오류 모달을 띄움
    }
  };

  const closeErrorModal = () => {
    setIsErrorModalVisible(false);
  };

  const handleCloseModal = () => {
    setIsVisible(false);
    onClose();
    window.location.reload(); // 페이지 새로고침
  };

  const confirmNBbang = () => {
    setIsConfirmModalVisible(false);
    handleSplit2(); // N빵 진행
  };

  const closeSuccessModal = () => {
    setIsSuccessModalVisible(false);
    onClose();
    window.location.reload(); // 페이지 새로고침
  };

  const cancelNBbang = () => {
    setIsConfirmModalVisible(false); // N빵 확인 모달만 닫기
  };

  return (
    <div className="relative">
      {/* N빵 나누기 모달 */}
      {isVisible && (
        <div
          className="fixed bottom-0 w-full max-w-[360px] mx-auto mt-4"
          style={{ zIndex: 901 }}
        >
          <div
            role="presentation"
            className="bg-white p-4 rounded-t-[40px] shadow-lg border border-gray-300 h-full flex flex-col items-center justify-center"
            onClick={(e) => e.stopPropagation()}
          >
            <h1 className="text-2xl font-bold text-center flex m-auto items-center text-black mb-4">
              N빵 나누기
            </h1>
            <div className="mb-2">
              <p className="text-gray-600">출금 계좌: {withdrawalAccountNo}</p>
            </div>

            {/* 스크롤 가능한 목록 */}
            <div className="flex-grow overflow-y-auto space-y-2 mb-4 max-h-[300px] w-full">
              {depositAccountList?.map((member) => (
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
                          member.role === "ADMIN"
                            ? kingBeanProfile
                            : beanProfile
                        }
                        alt="profile"
                        className="w-8 h-8 object-cover rounded-full"
                      />
                    </span>
                    <div className="flex flex-col">
                      <p className="text-sm text-gray-800">{member.userName}</p>
                      <p className="text-xs text-gray-500">
                        {member.mainAccountNumber || "계좌번호 없음"}
                      </p>
                    </div>
                  </div>
                  <p className="font-bold text-sm text-[#2F8F4E]">
                    {/* ₩ {dividedAmount.toLocaleString()} */}
                  </p>
                </div>
              ))}
            </div>

            <div className="flex justify-between mt-4 w-full">
              <button
                type="button"
                onClick={handleCloseModal}
                className="btn-lg bg-gray-200 text-gray-600 hover:bg-gray-300 w-[115px]"
              >
                취소
              </button>
              <button
                type="button"
                onClick={handleSplit}
                className="btn-lg text-white hover:bg-green-600 w-[115px]"
                disabled={selectedMembers.length === 0} // 멤버가 선택되지 않으면 비활성화
              >
                확인
              </button>
            </div>
          </div>
        </div>
      )}

      {/* N빵 확인 모달 */}
      {isConfirmModalVisible && (
        <div
          className="fixed inset-0 flex items-center justify-center bg-gray-900 bg-opacity-50"
          style={{ zIndex: 902 }}
        >
          <div className="bg-white p-8 rounded-lg shadow-lg">
            <h1 className="text-xl font-bold text-center mb-4">
              진짜 N빵 하시나요?
            </h1>
            <h2>₩ {dividedAmount.toLocaleString()}씩 나누기</h2>
            <div className="flex justify-between w-full mt-4">
              <button
                type="button"
                onClick={cancelNBbang}
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
          </div>
        </div>
      )}

      {/* N빵 성공 모달 */}
      {isSuccessModalVisible && (
        <div
          className="fixed inset-0 flex items-center justify-center bg-gray-900 bg-opacity-50"
          style={{ zIndex: 903 }}
        >
          <div className="bg-white p-8 rounded-lg shadow-lg">
            <h1 className="text-xl font-bold text-center mb-4 m-auto">
              N빵이 완료되었습니다!
            </h1>
            <img
              src={checkIcon}
              alt="success"
              className="w-16 h-16 mb-6 m-auto"
            />
            <button
              type="button"
              onClick={closeSuccessModal}
              className="px-4 py-2 rounded bg-primary text-white w-full hover:bg-green-600"
            >
              닫기
            </button>
          </div>
        </div>
      )}

      {/* N빵 실패 모달 */}
      {isErrorModalVisible && (
        <div
          className="fixed inset-0 flex items-center justify-center bg-gray-900 bg-opacity-50"
          style={{ zIndex: 999 }}
        >
          <div className="bg-white p-8 rounded-lg shadow-lg max-w-[330px]">
            <h1 className="text-xl font-bold text-center mb-4">
              N빵에 실패하였습니다!
            </h1>
            <img src={warningIcon} alt="fail" className="w-16 h-16 m-auto" />
            <p className="text-gray-800 text-center mb-4">
              선택된 멤버 중 메인 계좌가 설정되지 않은 사람이 있습니다. 설정 후
              다시 시도해주세요.
            </p>
            <button
              type="button"
              onClick={closeErrorModal}
              className="px-4 py-2 rounded bg-red-600 text-white w-full hover:bg-red-700"
            >
              확인
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default ExchangeSplit;
