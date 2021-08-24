#!/bin/sh
# (c) https://github.com/MontiCore/monticore

docker run --rm -d --net=host -p 3000:3000 --name logtracer_frontend registry.git.rwth-aachen.de/monticore/montithings/core/logtracer_frontend
