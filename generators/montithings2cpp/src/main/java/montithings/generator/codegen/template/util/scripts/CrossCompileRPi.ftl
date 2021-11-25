<#-- (c) https://github.com/MontiCore/monticore -->
#!/bin/sh
# (c) https://github.com/MontiCore/monticore
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/Preamble.ftl">

# Cross compiles the application for Raspberry Pi 4

if [ $(which docker >> /dev/null) -ne 0 ]
then
echo "Cross compiling requires Docker. It looks like Docker isn't installed. Aborting."
exit 1
fi

docker run --rm dockcross/linux-armv7-lts > ./dockcross
chmod +x dockcross
<#if config.getSplittingMode().toString() == "OFF">
  ./dockcross bash -c './build.sh ${comp.getPackageName()}'
<#else>
  ./dockcross bash -c './build.sh ${comp.getFullName()}'
</#if>
