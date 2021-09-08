# (c) https://github.com/MontiCore/monticore
cd ../..
docker build . --file services/prolog-generator/Dockerfile --network host --tag registry.git.rwth-aachen.de/monticore/montithings/core/prolog-generator:latest
