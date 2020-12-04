${tc.signature("comp", "config")}

<#if config.getMessageBroker().toString() == "DDS">
    FROM registry.git.rwth-aachen.de/monticore/montithings/core/mtcmakedds AS build
<#else>
    FROM registry.git.rwth-aachen.de/monticore/montithings/core/mtcmake AS build
</#if>

# Switch into our apps working directory
WORKDIR /usr/src/app/

# Copy all the app source into docker context
COPY . /usr/src/app/

# In case it was build without docker before
RUN rm -rf build

# Build our binary/binaries
<#-- comp is the application -->
<#if config.getSplittingMode().toString() == "OFF">
RUN ./build.sh ${comp.getPackageName()}
<#else>
RUN ./build.sh ${comp.getFullName()}
</#if>

<#-- the dds build image is based on ubuntu, thus we have to distinguish -->
<#if config.getMessageBroker().toString() == "DDS">
FROM ubuntu:groovy
<#else>
FROM alpine

RUN apk add --update-cache g++ 
</#if>

<#if config.getMessageBroker().toString() == "MQTT">
RUN apk add --update-cache mosquitto
</#if>

<#if config.getSplittingMode().toString() != "OFF" && config.getMessageBroker().toString() == "DDS">
RUN apt update && apt install multitail
<#elseif config.getSplittingMode().toString() != "OFF">
RUN apk add --update-cache multitail
</#if>

COPY --from=build /usr/src/app/build/bin /usr/src/app/build/bin 

WORKDIR /usr/src/app/build/bin

<#if config.getSplittingMode().toString() == "OFF">
<#if config.getMessageBroker().toString() == "DDS">
RUN echo './${comp.getFullName()} -DCPSConfigFile dcpsconfig.ini "$@"' > entrypoint.sh
<#else>
RUN echo './${comp.getFullName()} "$@"' > entrypoint.sh
</#if>
<#else>
RUN echo "./run.sh" > entrypoint.sh
RUN echo "multitail  ${comp.getFullName()}.*" >> entrypoint.sh
</#if>


# Run our binary on container startup
ENTRYPOINT [ "sh", "entrypoint.sh" ]