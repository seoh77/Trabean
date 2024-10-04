/** @type {import("tailwindcss").Config} */
module.exports = {
  content: ["./index.html", "./src/**/*.{js,jsx,ts,tsx}"],
  theme: {
    extend: {
      colors: {
        "shadow-color": "rgba(0, 0, 0, 0.5)", // 사용자 정의 그림자 색상 추가
        primary: {
          DEFAULT: "#6FA760",
          light: "#C0DEB8",
        },
        yellow: "#FEC72A",
      },
    },
    fontFamily: {
      Pretendard: ["Pretendard"],
    },
  },
  plugins: [],
};
