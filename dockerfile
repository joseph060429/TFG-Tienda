FROM node:20-alpine
# RUN npm install -g yarn
RUN mkdir /app
WORKDIR /app
COPY package*.json ./
RUN yarn install --frozen-lockfile && \
    yarn cache clean
COPY . .
EXPOSE 3000
CMD [ "yarn", "dev", "--host","--dotenv", ".env.development" ]

# docker build -t osephsm060429/frontend:v1 .
# docker push maruizlosada/frontend-tfg:v1