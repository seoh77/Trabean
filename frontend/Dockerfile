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


EXPOSE 8888
CMD ["nginx", "-g", "daemon off;"]
