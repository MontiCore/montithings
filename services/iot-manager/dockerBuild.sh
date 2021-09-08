# (c) https://github.com/MontiCore/monticore
mvn clean install -DskipTests
docker build --network=host -t registry.git.rwth-aachen.de/monticore/montithings/core/deployment-server .
