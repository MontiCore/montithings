mvn clean install -DskipTests
docker build --network=host -t deployment-server .
