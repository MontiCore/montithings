#!/bin/sh
# (c) https://github.com/MontiCore/monticore

#./clear.sh

PATHCURR="$(pwd)"

# Build RTE
mkdir -p build/rte
cd build/rte || exit
cmake -D EXCLUDE_MQTT:BOOLEAN=0 \
      -D EXCLUDE_DDS:BOOLEAN=0 \
      -D EXCLUDE_COMM_MANAGER:BOOLEAN=1 \
      -G Ninja \
      "../../../../generators/montithings2cpp/src/main/resources/rte/montithings-RTE"
ninja
cd ..

# Build logtracer_middleware
mkdir -p logtracer_middleware
cd logtracer_middleware || exit
cmake -G Ninja ../../
ninja

cp ../../dcpsconfig.ini bin/.

cd "$PATHCURR" || exit