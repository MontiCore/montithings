# (c) https://github.com/MontiCore/monticore

# Build Image -----------------------------
FROM registry.git.rwth-aachen.de/monticore/montithings/core/mtcmakedds AS build

# Switch into working directory
WORKDIR /usr/src/app/services/recorder

# Copy all the app source into docker context
COPY services/recorder /usr/src/app/services/recorder

# Copy rte
COPY generators/montithings2cpp/src/main/resources/rte/montithings-RTE \
    /usr/src/app/generators/montithings2cpp/src/main/resources/rte/montithings-RTE


# In case it was build without docker before
RUN rm -rf build

# Build our binary/binaries
RUN ./build.sh


RUN mkdir /usr/src/app/services/recorder/recordings

# Run the container
ENTRYPOINT [ "build/recorder/bin/recorder", "--fileRecordings", "/usr/src/app/services/recorder/recordings/recordings.json" ]