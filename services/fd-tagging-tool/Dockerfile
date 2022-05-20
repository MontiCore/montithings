FROM openjdk:8

RUN apt-get update -y

# Copy main JAR file
COPY ./target/libs/TaggingToolServer.jar /usr/app/TaggingToolServer.jar

WORKDIR /usr/app

ENTRYPOINT [ "java", "-jar", "TaggingToolServer.jar" ]
