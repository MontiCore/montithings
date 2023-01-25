# (c) https://github.com/MontiCore/monticore
FROM gradle:6.9.1-jdk11 as build

COPY src/main/resources/languages /src/main/app/languages
COPY src/main/resources/languages/hierarchy/Sink/src/main/resources/templates /src/main/app/templates
COPY target/generated-sources/generator-server/src /src/main/app/src
COPY target/generated-sources/generator-server/build.gradle /src/main/app/
COPY target/generated-sources/generator-server/settings.gradle /src/main/app/
COPY target/generated-sources/generator-server/pom.xml /src/main/app/
WORKDIR /src/main/app
RUN gradle --no-daemon clean build



FROM openjdk:11-jre

COPY --from=build /src/main/app/languages /src/main/app/languages
COPY --from=build /src/main/app/build/libs /src/main/app
COPY --from=build /src/main/app/templates /src/main/app/templates

WORKDIR /src/main/app
ENTRYPOINT [ "java", "-cp", "GeneratorServer.jar", "-Dorg.eclipse.jetty.util.log.class=org.eclipse.jetty.util.log.StdErrLog", "Main" ]
