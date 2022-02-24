<#-- (c) https://github.com/MontiCore/monticore -->
#!/bin/sh
${tc.signature("hwcPythonScripts","config", "existsHWC")}

#
# Set "export USE_CONAN=1" to make this script call conan
# Or call "USE_CONAN=1 ./build.sh componentName"
#


set -e # Stop on first error

if [ "$#" -ne 1 ]
then
echo "Please provide the component you want to run as first argument. Aborting."
exit 1
fi

if [ ! -d "$1" ]
then
echo "There is no component whose fully qualified name matches the first argument. Aborting."
exit 1
fi

<#if config.getTargetPlatform().toString() == "DSA_VCG">
  dev-docker.sh l06 build
  cd build_dev-l06_*
<#else>
  mkdir -p build
  cd build
  if [ -n "${r"${USE_CONAN}"}" ] && [ "${r"${USE_CONAN}"}" = "1" ]
  then
  conan install --build missing ..
  fi
    <#if config.getTargetPlatform().toString() == "DSA_LAB">
      $CMAKE -G Ninja ..
    <#else>
      cmake -G Ninja ..
    </#if>
  ninja
</#if>

<#if !(config.getSplittingMode().toString() == "OFF")>
echo Copy Scripts for "$1"
cd ..
find hwc -name "*.py" | cpio -pdm build/bin/ > /dev/null 2>&1
cd build/bin
cp ../../"$1"/*.sh .
<#if config.getMessageBroker().toString() == "DDS">
cp ../../"$1"/*.ini .
</#if>
<#if config.getMessageBroker().toString() == "MQTT" && hwcPythonScripts?size!=0>
mkdir python
cp ../../python/sensoractuatormanager.py python/.
cp ../../python/montithingsconnector.py python/.
cp ../../python/requirements.txt python/.
</#if>
<#if config.getSplittingMode().toString() == "LOCAL">
cp -r ../../"$1"/ports .
</#if>
<#if config.getReplayMode().toString() == "ON">
cp ../../../../recordings.json .
</#if>

chmod +x *.sh
cd ../..
</#if>

