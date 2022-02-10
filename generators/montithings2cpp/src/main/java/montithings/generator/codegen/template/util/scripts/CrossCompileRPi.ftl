<#-- (c) https://github.com/MontiCore/monticore -->
#!/bin/sh
# (c) https://github.com/MontiCore/monticore
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/Preamble.ftl">

# Cross compiles the application for Raspberry Pi 4

# Check if a command is available on this system
# Taken from https://get.docker.com/
command_exists() {
        command -v "$@" > /dev/null 2>&1
}

if ! command_exists docker
then
echo "Cross compiling requires Docker. It looks like Docker isn't installed. Aborting."
exit 1
fi


docker run --rm --platform=linux/amd64 dockcross/linux-armv7-lts > ./dockcross
chmod +x dockcross
<#if config.getSplittingMode().toString() == "OFF">
  ./dockcross -a --platform=linux/amd64 bash -c './build.sh ${comp.getPackageName()}'
<#else>
  ./dockcross -a --platform=linux/amd64 bash -c './build.sh ${comp.getFullName()}'
</#if>
