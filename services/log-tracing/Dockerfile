# (c) https://github.com/MontiCore/monticore

# Build Image -----------------------------
FROM registry.git.rwth-aachen.de/monticore/montithings/core/mtcmakedds AS build

RUN apt-get update && apt-get install -y \
    libboost-all-dev \
    libmosquitto-dev

# Switch into working directory
WORKDIR /usr/src/app/services/log-tracing

# Copy all the app source into docker context
COPY services/log-tracing /usr/src/app/services/log-tracing

# Copy rte
COPY generators/montithings2cpp/src/main/resources/rte/montithings-RTE \
    /usr/src/app/generators/montithings2cpp/src/main/resources/rte/montithings-RTE

# In case it was build without docker before
RUN rm -rf build

# Build our binary/binaries
RUN ./build.sh

FROM ubuntu:groovy

COPY services/log-tracing/entrypoint.sh .

COPY --from=build /usr/src/app/services/log-tracing/build/logtracer_middleware/bin/logtracer_middleware .

# Run the container
ENTRYPOINT [ "sh", "entrypoint.sh" ]
CMD [ "/bin/sh" ]