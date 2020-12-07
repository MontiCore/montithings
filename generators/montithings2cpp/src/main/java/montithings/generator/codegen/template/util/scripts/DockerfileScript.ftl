<#-- (c) https://github.com/MontiCore/monticore -->
# (c) https://github.com/MontiCore/monticore
${tc.signature("comp", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign instances = ComponentHelper.getInstances(comp)>

# Build Image -----------------------------
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
# -----------------------------------------

<#if config.getSplittingMode().toString() == "OFF">
    # COMPONENT: ${comp.getFullName()}
    <#-- the dds build image is based on ubuntu, thus we have to distinguish -->
    <#if config.getMessageBroker().toString() == "DDS">
    FROM ubuntu:groovy AS ${comp.getFullName()?lower_case}
    <#else>
    FROM alpine AS ${comp.getFullName()?lower_case}

    RUN apk add --update-cache g++ 
    </#if>

    <#if config.getMessageBroker().toString() == "MQTT">
    RUN apk add --update-cache mosquitto
    </#if>

    COPY --from=build /usr/src/app/build/bin/${comp.getFullName()} /usr/src/app/build/bin/

    WORKDIR /usr/src/app/build/bin

    RUN echo './${comp.getFullName()} "$@"' > entrypoint.sh

    # Run our binary on container startup
    ENTRYPOINT [ "sh", "entrypoint.sh" ]

<#else>
    <#list instances as pair >
        # COMPONENT: ${pair.getKey().fullName}
        <#-- the dds build image is based on ubuntu, thus we have to distinguish -->
        <#if config.getMessageBroker().toString() == "DDS">
        FROM ubuntu:groovy AS ${pair.getKey().fullName}
        <#else>
        FROM alpine AS ${pair.getKey().fullName}

        RUN apk add --update-cache g++ 
        </#if>

        <#if config.getMessageBroker().toString() == "MQTT">
        RUN apk add --update-cache mosquitto
        </#if>


        COPY --from=build /usr/src/app/build/bin/${pair.getKey().fullName} /usr/src/app/build/bin/

        WORKDIR /usr/src/app/build/bin

        RUN echo './${pair.getKey().fullName} "$@"' > entrypoint.sh

        # Run our binary on container startup
        ENTRYPOINT [ "sh", "entrypoint.sh" ]
    </#list>
</#if>