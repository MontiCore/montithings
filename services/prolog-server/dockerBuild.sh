# (c) https://github.com/MontiCore/monticore
cd ../..
docker build . --file services/prolog-server/Dockerfile --network host --tag montithings/prolog-generator:latest
