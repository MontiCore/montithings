#!/bin/sh

#./clear.sh

PATHCURR="$(pwd)"

# Build RTE
mkdir -p build/rte
cd build/rte || exit
cmake -D EXCLUDE_MQTT:BOOLEAN=1 \
      -G Ninja \
      "../../../../generators/montithings2cpp/src/main/resources/rte/montithings-RTE"
ninja
cd ..

# Build recorder
mkdir -p recorder
cd recorder || exit
cmake -G Ninja ../../
ninja

cp ../../dcpsconfig.ini bin/.

cd "$PATHCURR" || exit