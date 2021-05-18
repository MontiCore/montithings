#!/bin/sh
# (c) https://github.com/MontiCore/monticore

cd frontend
yarn serve &
exec ./logtracer_middleware "$@"