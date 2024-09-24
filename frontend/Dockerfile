# build stage
FROM node:18 AS build-stage
WORKDIR /app
COPY package*.json ./

# Yarn is already installed
RUN yarn install
COPY . .
RUN yarn build

# production stage
FROM nginx:stable-alpine AS production-stage
COPY --from=build-stage /app/build /usr/share/nginx/html

# copy the custom nginx configuration file
COPY nginx.conf /etc/nginx/nginx.conf
COPY /etc/letsencrypt/live/j11a604.p.ssafy.io/cert.pem /etc/letsencrypt/live/j11a604.p.ssafy.io/cert.pem
COPY /etc/letsencrypt/live/j11a604.p.ssafy.io/privkey.pem /etc/letsencrypt/live/j11a604.p.ssafy.io/privkey.pem

EXPOSE 443
CMD ["nginx", "-g", "daemon off;"]
