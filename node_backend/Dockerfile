FROM node:18-alpine

ARG APP_NAME=api_gateway

WORKDIR /app

COPY api_gateway/package*.json ./

RUN npm install

COPY api_gateway/. .

CMD ["npm", "run", "start:prod"]