# Stage 2.1: Build frontend React App
FROM node:20 AS frontend-build

WORKDIR /webapp
COPY webapp/package*.json ./

RUN npm install

COPY webapp/ .

RUN npm run build:prod

# Stage 2.2: Create lightweight runtime image
FROM node:20 AS frontend-runtime

COPY --from=frontend-build /webapp/build /usr/share/nginx/html

EXPOSE 80

CMD ["npx", "serve", "-s", "/usr/share/nginx/html", "-l", "80"]