FROM openjdk:8

# Install SWI Prolog
RUN apt-get update -y
RUN apt-get install -y swi-prolog-java
RUN ln -s /usr/lib/swi-prolog/lib/x86_64-linux/libjpl.so /usr/lib/libjpl.so

# Copy main JAR file
COPY ./target/DeploymentServer-0.0.1-SNAPSHOT-jar-with-dependencies.jar /usr/app/DeploymentServer.jar

WORKDIR /usr/app

ENTRYPOINT [ "java", "-jar", "DeploymentServer.jar" ]
