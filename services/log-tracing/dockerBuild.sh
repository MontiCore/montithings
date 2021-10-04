#!/bin/sh
# (c) https://github.com/MontiCore/monticore

cd frontend
./dockerBuild.sh
cd -

# cd to root directory /core.
# this is necessary, as we need to copy the rte directory which would have been out of context otherwise
cd ../..

docker build -t montithings/logtracer-middleware -f services/log-tracing/Dockerfile .

cd - || exit
