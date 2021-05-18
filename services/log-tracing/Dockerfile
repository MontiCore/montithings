# (c) https://github.com/MontiCore/monticore

# Build Image -----------------------------
FROM registry.git.rwth-aachen.de/monticore/montithings/core/mtcmakedds AS build


RUN apt-get update && apt-get install -y \
    npm \
    libboost-all-dev

RUN npm install --global yarn

# Switch into working directory
WORKDIR /usr/src/app/services/log-tracing

# Copy all the app source into docker context
COPY services/log-tracing /usr/src/app/services/log-tracing

# Copy rte
COPY generators/montithings2cpp/src/main/resources/rte/montithings-RTE \
    /usr/src/app/generators/montithings2cpp/src/main/resources/rte/montithings-RTE

COPY services/log-tracing/entrypoint.sh .

RUN cd frontend

RUN yarn install

# In case it was build without docker before
RUN rm -rf build

# Build our binary/binaries
RUN ./build.sh

# Run the container
ENTRYPOINT [ "build/recorder/bin/logtracer_middleware" ]