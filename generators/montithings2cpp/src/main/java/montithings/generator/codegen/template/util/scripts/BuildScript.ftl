<#-- (c) https://github.com/MontiCore/monticore -->
#!/bin/sh
${tc.signature("comp","hwcPythonScripts","config", "existsHWC")}

#
# Set "export USE_CONAN=1" to make this script call conan
# Or call "USE_CONAN=1 ./build.sh componentName"
#


set -e # Stop on first error

SCRIPTPATH="$( cd -- "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"
<#if config.getSplittingMode().toString() == "OFF">
  COMPNAME=${comp.getPackageName()}
<#else>
  COMPNAME=${comp.getFullName()}
</#if>

COMPPATH=$SCRIPTPATH/$COMPNAME


if [ "$#" -eq 1 ]
then
COMPNAME="$1"
COMPPATH="$PWD/$1"
else
echo Using "$COMPNAME" as outermost component
fi

if [ ! -d "$COMPPATH" ]
then
echo "There is no component whose fully qualified name matches the first argument. Aborting."
exit 1
fi

CALLER_PWD="$PWD"
cd "$SCRIPTPATH" > /dev/null

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

<#if config.getSplittingMode().toString() != "OFF">
echo Copy Scripts for "$COMPNAME"
cd ..
find hwc -name "*.py" | cpio -pdm build/bin/ > /dev/null 2>&1
cd build/bin
cp ../../"$COMPNAME"/*.sh .
<#if config.getMessageBroker().toString() == "DDS">
cp ../../"$COMPNAME"/*.ini .
</#if>
<#if config.getMessageBroker().toString() == "MQTT" && hwcPythonScripts?size!=0>
mkdir python
cp ../../python/sensoractuatormanager.py python/.
cp ../../python/montithingsconnector.py python/.
cp ../../python/requirements.txt python/.
</#if>
<#if config.getSplittingMode().toString() == "LOCAL">
cp -r ../../"$COMPNAME"/ports .
</#if>
<#if config.getReplayMode().toString() == "ON">
cp ../../../../recordings.json .
</#if>

chmod +x *.sh
cd ../..
</#if>

cd "$CALLER_PWD" > /dev/null

