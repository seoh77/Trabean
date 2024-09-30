import logo from "../../assets/logo.png";
import plusIcon from "../../assets/icon/plusIcon.png";

function MainPage() {
  return (
    <div className="px-6 py-20">
      <img src={logo} alt="로고" className="h-5 mb-3" />
      <div className="bg-primary-light h-56 rounded-2xl px-4 py-5 flex flex-col justify-between">
        <div>
          <h4 className="text-lg font-bold">통장명</h4>
          <span className="text-sm">계좌번호</span>
        </div>
        <div className="text-3xl font-bold">금액</div>
        <button type="button" className="btn-lg">
          이체하기
        </button>
      </div>
      <div className="border-[1.5px] border-primary border-solid min-h-64 mt-5 rounded-xl" />
      <div className="mt-5 border-[1.5px] border-primary-light border-solid rounded-lg flex justify-center items-center h-11">
        <img src={plusIcon} alt="통장추가버튼" className="h-5" />
      </div>
    </div>
  );
}

export default MainPage;
