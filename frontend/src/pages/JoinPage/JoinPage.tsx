import prevIcon from "../../assets/icon/prevIcon.png";

function JoinPage() {
  return (
    <div className="h-[100vh] relative mt-5">
      <div className="flex">
        <img src={prevIcon} alt="이전버튼" className="w-[17px] h-[28px]" />
        <h2 className="text-lg m-auto">회원가입</h2>
      </div>
      <div className="mx-2">
        <div className="flex items-center justify-between mt-16 mb-5">
          <input
            type="text"
            name="email"
            id="email"
            placeholder="아이디"
            className="w-[130px] border-b-2 border-primary text-base pl-2"
          />
          <div>@</div>
          <select className="w-[100px]">
            <option value="" disabled>
              선택
            </option>
            <option value="gmail">gmail.com</option>
            <option value="naver">naver.com</option>
            <option value="daum">daum.net</option>
          </select>
          <button type="button" className="btn-light-md">
            중복확인
          </button>
        </div>
        <div className="mb-5">
          <input
            type="password"
            name="password"
            id="password"
            className="border-b-2 border-primary text-base pl-2 w-full focus:outline-none"
            placeholder="비밀번호"
          />
          <span className="text-sm text-gray-300 font-semibold">
            * 비밀번호 (특수문자 포함 8글자 이상)
          </span>
        </div>
        <input
          type="password"
          name="password"
          id="password"
          className="border-b-2 border-primary text-base pl-2 w-full focus:outline-none"
          placeholder="비밀번호 확인"
        />
      </div>
      <button type="button" className="btn-lg w-full absolute bottom-10">
        회원가입
      </button>
    </div>
  );
}

export default JoinPage;
