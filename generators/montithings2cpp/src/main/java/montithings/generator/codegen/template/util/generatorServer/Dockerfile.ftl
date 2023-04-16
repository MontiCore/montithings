${tc.signature("languagePaths","existsHWC")}
<#include "/template/Preamble.ftl">
#
# GENERATED FILE. DO NOT EDIT. CHANGES WILL BE OVERWRITTEN!
#

# (c) https://github.com/MontiCore/monticore
FROM gradle:6.9.1-jdk11 as build

COPY src/main/resources/languages /src/main/app/languages
<#list languagePaths as path>
    COPY src/main/resources/languages${path}/src/main/resources/templates /src/main/app/templates${path}
</#list>

COPY target/generated-sources/generator-server/src/main/java/Main.javaFile /src/main/app/src/main/java/Main.java
COPY target/generated-sources/generator-server/build.gradle /src/main/app/
COPY target/generated-sources/generator-server/html /src/main/app/html
WORKDIR /src/main/app
RUN gradle --no-daemon clean build



FROM openjdk:11-jre

COPY --from=build /src/main/app/languages /src/main/app/languages
COPY --from=build /src/main/app/build/libs /src/main/app
COPY --from=build /src/main/app/templates /src/main/app/templates
COPY --from=build /src/main/app/html /src/main/app/html

WORKDIR /src/main/app
RUN echo 'java -cp GeneratorServer.jar -Dorg.eclipse.jetty.util.log.class=org.eclipse.jetty.util.log.StdErrLog Main "$@"' > entrypoint.sh
ENTRYPOINT [ "sh", "entrypoint.sh"]
