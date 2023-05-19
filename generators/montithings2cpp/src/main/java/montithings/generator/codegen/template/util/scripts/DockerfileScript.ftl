<#-- (c) https://github.com/MontiCore/monticore -->
# (c) https://github.com/MontiCore/monticore
${tc.signature("comp", "sensorActuatorPorts", "hwcPythonScripts", "config", "existsHWC")}
<#include "/template/Preamble.ftl">
<#assign instances = ComponentHelper.getExecutableInstances(comp, config)>

# Build Image -----------------------------
<#if brokerIsDDS>
    FROM montithings/mtcmakedds AS build
<#else>
    FROM montithings/mtcmake AS build
</#if>

# Switch into our apps working directory
WORKDIR /usr/src/app/

# Copy all the app source into docker context
COPY . /usr/src/app/

# In case it was build without docker before
RUN rm -rf build

# Build our binary/binaries
<#-- comp is the application -->
<#if splittingModeDisabled>
RUN ./build.sh ${comp.getPackageName()}
<#else>
RUN ./build.sh ${comp.getFullName()}
</#if>
# -----------------------------------------

<#if splittingModeDisabled>
    # COMPONENT: ${comp.getFullName()}
    <#-- the dds build image is based on ubuntu, thus we have to distinguish -->
    <#if brokerIsDDS>
    FROM ubuntu:groovy AS ${comp.getFullName()?lower_case}
    <#else>
    FROM alpine AS ${comp.getFullName()?lower_case}

    RUN apk add --update-cache --force-overwrite libgcc libstdc++ libressl-dev libpq-dev postgresql-client
    </#if>

    <#if brokerIsMQTT>
    ADD deployment-config.json /.montithings/deployment-config.json

    RUN apk add --update-cache mosquitto-libs++
    </#if>

    COPY --from=build /usr/src/app/build/bin/${comp.getFullName()} /usr/src/app/build/bin/

    WORKDIR /usr/src/app/build/bin

    RUN echo './${comp.getFullName()} "$@"' > entrypoint.sh

    # Run our binary on container startup
    ENTRYPOINT [ "sh", "entrypoint.sh" ]

<#else>
    <#-- helper list to detect duplicated keys -->
    <#assign processedInstances = [] />

    <#list instances as pair >
        <#if ! processedInstances?seq_contains(pair.getKey().fullName)>
            <#assign processedInstances = processedInstances + [pair.getKey().fullName] />

            # COMPONENT: ${pair.getKey().fullName}
            <#-- the dds build image is based on ubuntu, thus we have to distinguish -->
            <#if brokerIsDDS>
            FROM debian:buster AS ${pair.getKey().fullName}
            <#else>
            FROM alpine AS ${pair.getKey().fullName}

            RUN apk add --update-cache --force-overwrite libgcc libstdc++ libressl-dev libpq-dev postgresql-client
            </#if>

            <#if brokerIsMQTT>
            ADD deployment-config.json /.montithings/deployment-config.json

            RUN apk add --update-cache --force-overwrite mosquitto-libs++ libressl-dev libpq-dev postgresql-client
            </#if>
            COPY --from=build /usr/src/app/build/bin/${pair.getKey().fullName} /usr/src/app/build/bin/

            WORKDIR /usr/src/app/build/bin

            RUN echo './${pair.getKey().fullName} "$@"' > entrypoint.sh

            # Run our binary on container startup
            ENTRYPOINT [ "sh", "entrypoint.sh" ]
        </#if>
    </#list>
</#if>
<#if brokerIsMQTT>
    <#list sensorActuatorPorts as port >

            # SENSORACTUATOR: ${port}
            FROM alpine AS ${port}

            RUN apk add --update-cache g++

            RUN apk add --update-cache mosquitto

            ADD deployment-config.json /.montithings/deployment-config.json

            COPY --from=build /usr/src/app/build/bin/${port} /usr/src/app/build/bin/

            WORKDIR /usr/src/app/build/bin

            RUN echo './${port} "$@"' > entrypoint.sh

            # Run our binary on container startup
            ENTRYPOINT [ "sh", "entrypoint.sh" ]
    </#list>
    <#list hwcPythonScripts as script >
            <#assign splitScript  = script?split(".")>

            # PYTHON SCRIPT: ${script}
            FROM alpine AS ${script}

            RUN apk add --no-cache python3 py3-pip

            RUN apk add --update-cache mosquitto

            ADD deployment-config.json /.montithings/deployment-config.json

            COPY --from=build /usr/src/app/python/montithingsconnector.py /usr/src/app/build/bin/
            COPY --from=build /usr/src/app/python/parse_cmd.py /usr/src/app/build/bin/

            COPY --from=build /usr/src/app/build/bin/python/requirements.txt /usr/src/app/build/bin/

            COPY --from=build /usr/src/app/build/bin/hwc/${splitScript[0]}/${splitScript[1]}.py /usr/src/app/build/bin/

            WORKDIR /usr/src/app/build/bin

            RUN pip install -r requirements.txt

            # Run our binary on container startup
            ENTRYPOINT [ "python3", "${splitScript[1]}.py" ]

    </#list>
    <#if hwcPythonScripts?size!=0>
            FROM alpine AS sensoractuatormanager

            RUN apk add --no-cache python3 py3-pip

            RUN apk add --update-cache mosquitto

            ADD deployment-config.json /.montithings/deployment-config.json

            COPY --from=build /usr/src/app/python/parse_cmd.py /usr/src/app/build/bin/
            COPY --from=build /usr/src/app/python/requirements.txt /usr/src/app/build/bin/

            COPY --from=build /usr/src/app/python/sensoractuatormanager.py /usr/src/app/build/bin/

            WORKDIR /usr/src/app/build/bin

            RUN pip install -r requirements.txt

            ENTRYPOINT [ "python3", "./sensoractuatormanager.py" ]
        </#if>
</#if>