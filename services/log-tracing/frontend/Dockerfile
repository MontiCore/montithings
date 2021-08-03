# (c) https://github.com/MontiCore/monticore

FROM node

RUN mkdir /usr/src/app
WORKDIR /usr/src/app

ENV PATH /usr/src/app/node_modules/.bin:$PATH

COPY package.json /usr/src/app/package.json
RUN yarn install

COPY . .

CMD ["yarn", "serve"]