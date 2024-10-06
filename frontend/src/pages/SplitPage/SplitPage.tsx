import React, { useState, useEffect } from "react";
import beanProfile from "../../assets/bean_profile.png";
// import axios from 'axios'; // Axios를 사용하는 부분은 주석 처리

interface RequestData {
  totalAmount: number;
  totalNo: number;
  withdrawalAccountId: number;
  withdrawalAccountNo: string;
  depositAccountList: string[];
}

const ExchangeSplit: React.FC = () => {
  const [isVisible, setIsVisible] = useState(false); // State to control modal visibility
  const [step, setStep] = useState(1);
  const [requestData, setRequestData] = useState<RequestData | null>(null);
  const [dividedAmount, setDividedAmount] = useState<number>(0);

  // 추후 민우님 메인페이지 완료시 합칠예정
  useEffect(() => {
    // Axios로 데이터 요청하는 부분 주석 처리
    // axios
    //   .post<RequestData>("/api/travel/split", {
    //   })
    //   .then((response) => {
    //     setRequestData(response.data);
    //     setDividedAmount(Math.floor(response.data.totalAmount / response.data.totalNo));
    //   })
    //   .catch((error) => {
    //     console.error('Error fetching data:', error);
    //   });

    // 하드코딩된 예시 데이터
    const exampleData: RequestData = {
      totalAmount: 100000,
      totalNo: 4,
      withdrawalAccountId: 1234567890,
      withdrawalAccountNo: "356-0630-5770-33",
      depositAccountList: [
        "농협 356-0630-5770-33",
        "우리은행 1002-555-139750",
        "하나은행 123-456-789012",
      ],
    };

    setRequestData(exampleData);
    setDividedAmount(Math.floor(exampleData.totalAmount / exampleData.totalNo));
  }, []);

  const nextStep = () => {
    if (step < 4) setStep(step + 1);
  };

  const prevStep = () => {
    if (step > 1) setStep(step - 1);
  };

  const toggleModal = () => {
    setStep(1); // Reset step when modal opens
    setIsVisible(!isVisible);
  };

  if (!requestData) {
    return <p>Loading...</p>;
  }

  return (
    <div className="flex flex-col items-center h-screen bg-[#F2F9F4]">
      {/* Button to open the modal */}
      <button
        type="button"
        onClick={toggleModal}
        className="px-4 py-2 bg-[#2F8F4E] text-white bottom rounded shadow hover:bg-green-600"
      >
        N빵 나누기 시작하기
      </button>

      {/* Modal Wrapper */}
      <div className="relative w-full max-w-md mt-4">
        {/* Modal */}
        <div
          className={`absolute left-0 right-0 transition-transform transform ${
            isVisible ? "translate-y-0" : "translate-y-full"
          }`}
          style={{
            bottom: "0", // Position it at the bottom of the container
            transition: "transform 0.3s ease-in-out",
          }}
        >
          <div className="bg-white p-8 rounded-lg shadow-lg border border-gray-300">
            {step === 1 && (
              <>
                <div className="flex justify-between items-center mb-6">
                  <h1 className="text-2xl font-bold text-[#2F8F4E]">
                    N빵 나누기
                  </h1>
                  <button
                    type="button"
                    onClick={toggleModal} // Close button functionality
                    className="text-lg text-gray-500 hover:text-gray-700"
                  >
                    X
                  </button>
                </div>
                <div className="mb-4">
                  <p className="text-gray-600">
                    출금 계좌: {requestData.withdrawalAccountNo}
                  </p>
                </div>
                <div className="space-y-2">
                  {requestData.depositAccountList.map((account, index) => (
                    <div
                      // eslint-disable-next-line react/no-array-index-key
                      key={index}
                      className="flex justify-between items-center p-2 bg-[#F9F9F9] rounded shadow"
                    >
                      <div className="flex items-center">
                        <span className="mr-2 text-green-600">
                          <img
                            src={beanProfile}
                            alt="profile"
                            className="w-full h-full object-cover rounded-full"
                          />
                        </span>
                        <p className="text-gray-800">{account}</p>
                      </div>
                      <p className="font-bold text-lg text-[#2F8F4E]">
                        ₩ {dividedAmount.toLocaleString()}
                      </p>
                    </div>
                  ))}
                </div>
                <div className="flex justify-between mt-4">
                  <button
                    type="button"
                    onClick={toggleModal} // Close button functionality
                    className="px-4 py-2 bg-gray-200 text-gray-600 rounded shadow hover:bg-gray-300"
                  >
                    취소
                  </button>
                  <button
                    type="button"
                    onClick={nextStep}
                    className="px-4 py-2 bg-[#2F8F4E] text-white rounded shadow hover:bg-green-600"
                  >
                    확인
                  </button>
                </div>
              </>
            )}

            {step === 2 && (
              <>
                <h1 className="text-xl font-bold mb-4 text-center text-gray-800">
                  진짜 N빵 하시나요?
                </h1>
                <div className="flex justify-between mt-4">
                  <button
                    type="button"
                    onClick={toggleModal} // Close button functionality
                    className="px-4 py-2 bg-gray-200 text-gray-600 rounded shadow hover:bg-gray-300"
                  >
                    취소
                  </button>
                  <button
                    type="button"
                    onClick={nextStep}
                    className="px-4 py-2 bg-[#2F8F4E] text-white rounded shadow hover:bg-green-600"
                  >
                    확인
                  </button>
                </div>
              </>
            )}

            {step === 3 && (
              <>
                <h1 className="text-xl font-bold mb-4 text-center text-[#2F8F4E]">
                  N빵이 완료되었습니다!
                </h1>
                <div className="flex justify-center mt-4">
                  <span className="text-6xl text-[#2F8F4E]">✅</span>
                </div>
                <button
                  type="button"
                  onClick={toggleModal} // Close button functionality
                  className="mt-4 px-4 py-2 bg-[#2F8F4E] text-white w-full rounded shadow hover:bg-green-600"
                >
                  닫기
                </button>
              </>
            )}

            {step === 4 && (
              <>
                <h1 className="text-xl font-bold mb-4 text-center text-red-600">
                  N빵에 실패하였습니다!
                </h1>
                <div className="flex justify-center mt-4">
                  <span className="text-6xl text-red-600">⚠️</span>
                </div>
                <button
                  type="button"
                  onClick={prevStep}
                  className="mt-4 px-4 py-2 bg-red-600 text-white w-full rounded shadow hover:bg-red-700"
                >
                  다시 시도
                </button>
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ExchangeSplit;
