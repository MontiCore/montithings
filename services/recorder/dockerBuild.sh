#!/bin/sh
# (c) https://github.com/MontiCore/monticore

# cd to root directory /core.
# this is necessary, as we need to copy the rte directory which would have been out of context otherwise
cd ../..

docker build -t montithings/recorder -f services/recorder/Dockerfile .

cd - || exit
