# (c) https://github.com/MontiCore/monticore
mvn clean install -DskipTests
docker build --network=host -t montithings/iot-manager .
