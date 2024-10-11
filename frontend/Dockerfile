# 빌드 단계 설정
FROM node:20 AS build-stage
# 작업 디렉토리를 /app으로 설정
WORKDIR /app
# package.json과 package-lock.json 파일을 현재 작업 디렉토리로 복사
COPY package*.json ./

# Yarn을 사용하여 의존성 설치
RUN npm ci
# 모든 소스 파일을 현재 작업 디렉토리로 복사
COPY . .
# ARG 추가
ARG REACT_APP_END_POINT
ENV REACT_APP_END_POINT=$REACT_APP_END_POINT
# 애플리케이션 빌드
RUN npm run build
#RUN echo "REACT_APP_END_POINT: $REACT_APP_END_POINT" && yarn build

# 프로덕션 단계 설정
FROM nginx:alpine AS production-stage
# 빌드 단계에서 생성된 빌드 결과물을 Nginx의 기본 HTML 디렉토리로 복사
COPY --from=build-stage /app/build /usr/share/nginx/html

# 커스텀 Nginx 설정 파일을 Nginx의 설정 디렉토리로 복사
COPY nginx.conf /etc/nginx/nginx.conf

# Nginx가 사용할 포트 80을 노출
EXPOSE 8888
# Nginx를 포그라운드 모드로 실행하여 컨테이너가 계속 실행되도록 함
CMD ["nginx", "-g", "daemon off;"]

