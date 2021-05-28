#!/bin/sh
# (c) https://github.com/MontiCore/monticore
. /usr/src/app/opendds/setenv.sh

cd frontend
yarn serve &

cd ../build/logtracer_middleware/bin
./logtracer_middleware "$@"
