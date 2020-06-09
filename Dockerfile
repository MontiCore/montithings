FROM gradle:6.1-jdk11 as build

COPY . /src/main/app
WORKDIR /src/main/app
RUN gradle --no-daemon clean release



FROM openjdk:11-jre

COPY --from=build /src/main/app/target/jar /src/main/app
COPY --from=build /src/main/app/src/test/resources /src/main/app/resources

CMD [ "sh" ]
