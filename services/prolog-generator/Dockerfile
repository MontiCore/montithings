FROM gradle:6.1-jdk11 as build

COPY . /src/main/app
WORKDIR /src/main/app
RUN gradle --no-daemon clean release



FROM openjdk:11-jre

COPY --from=build /src/main/app/server/target/jar /src/main/app

WORKDIR /src/main/app
ENTRYPOINT [ "java", "-cp", "server.jar", "-Dorg.eclipse.jetty.util.log.class=org.eclipse.jetty.util.log.StdErrLog", "Main" ]
