import beanFull from "../../assets/beanFull.png";
import beanHalf from "../../assets/beanHalf.png";
import beanEmpty from "../../assets/beanEmpty.png";

// HotelCard 컴포넌트의 Props 타입 정의
interface CardProps {
  day: string | number;
  type: string;
  name: string;
  rating: number;
  review: number;
  address: string;
  summary: string;
  url: string;
}

// HotelCard 컴포넌트 구현
const Card: React.FC<CardProps> = ({
  day,
  name,
  type,
  rating,
  review,
  address,
  summary,
  url,
}) => {
  // 꽉 찬 완두콩, 반 채워진 완두콩, 빈 완두콩 개수 계산
  const fullBeans = Math.floor(rating);
  const halfBeans = rating % 1 >= 0.4 ? 1 : 0;
  const emptyBeans = 5 - fullBeans - halfBeans;

  return (
    <>
      {/* 버튼을 클릭하면 모달이 열리도록 설정 */}
      <button
        type="button"
        onClick={() => window.open(url, "_blank")}
        className="flex p-4 border rounded-xl shadow-md bg-white w-full text-left"
      >
        {/* 왼쪽 아이콘 영역 */}
        <div className="mr-1">
          <div className="text-lg font-bold text-gray-500">
            {day === 0 ? "❤" : day}
          </div>
        </div>

        {/* 중앙 텍스트와 이미지 영역 */}
        <div className="flex-grow">
          <div className="flex items-center">
            {/* 호텔 이름 */}
            <div className="text-md font-bold mb-1">
              {name}{" "}
              <span className="text-sm font-light text-gray-600">{type}</span>
            </div>
            {/* 호텔 주소 */}
            <div className="m-auto px-4 text-gray-500 text-xs w-32">
              {address}
            </div>
          </div>

          {/* 별점, 완두콩, 리뷰 개수 */}
          <div className="flex items-center mb-2">
            {/* 별점 */}
            <span className="text-primary text-md font-semibold mr-1">
              {rating}
            </span>

            {/* 완두콩 이미지 표시 */}
            <div className="flex">
              {/* 꽉 찬 콩 */}
              {Array.from({ length: fullBeans }).map(() => (
                <img
                  key={`full-${Date}`}
                  src={beanFull}
                  alt="Full bean"
                  className="w-4 h-4"
                />
              ))}

              {/* 반 채워진 콩 */}
              {Array.from({ length: halfBeans }).map(() => (
                <img
                  key={`half-${Date}`}
                  src={beanHalf}
                  alt="Half bean"
                  className="w-4 h-4"
                />
              ))}

              {/* 빈 콩 */}
              {Array.from({ length: emptyBeans }).map(() => (
                <img
                  key={`empty-${Date}`}
                  src={beanEmpty}
                  alt="Empty bean"
                  className="w-4 h-4"
                />
              ))}
            </div>

            {/* 리뷰 개수 */}
            <span className="text-gray-400 font-semibold ml-1 text-sm">
              ({review})
            </span>
          </div>

          <div className="border-t border-gray-300 my-2"> </div>

          {/* 호텔 요약 설명 */}
          <div className="text-center text-gray-700 text-xs mb-1">
            ❝{summary}❞
          </div>
        </div>
      </button>
    </>
  );
};

export default Card;
