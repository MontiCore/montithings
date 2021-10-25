# (c) https://github.com/MontiCore/monticore
cd ../..
docker build . --file services/prolog-generator/Dockerfile --network host --tag montithings/prolog-generator:latest
