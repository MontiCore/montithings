<#-- (c) https://github.com/MontiCore/monticore -->
#!/bin/sh
${tc.signature("comp","hwcPythonScripts","config", "existsHWC")}
<#include "/template/Preamble.ftl">

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

<#if targetPlatformIsDsaVcg>
  dev-docker.sh l06 build
  cd build_dev-l06_*
<#else>
  mkdir -p build
  cd build
  if [ -n "${r"${USE_CONAN}"}" ] && [ "${r"${USE_CONAN}"}" = "1" ]
  then
  conan install --build missing ..
  fi
    <#if targetPlatformIsDsaLab>
      $CMAKE -G Ninja ..
    <#else>
      cmake -G Ninja ..
    </#if>
  ninja
</#if>

<#if !(splittingModeDisabled)>
echo Copy Scripts for "$COMPNAME"
cd ..
find hwc -name "*.py" | cpio -pdm build/bin/ > /dev/null 2>&1
cd build/bin
cp ../../"$COMPNAME"/*.sh .

cp -r ../../html .
<#list ComponentHelper.getAllLanguageDirectories(config) as file>
  mkdir -p models${file}
</#list>
<#if brokerIsDDS>
cp ../../"$COMPNAME"/*.ini .
</#if>
<#if brokerIsMQTT && hwcPythonScripts?size!=0>
mkdir -p python
cp ../../python/sensoractuatormanager.py python/.
cp ../../python/montithingsconnector.py python/.
cp ../../python/requirements.txt python/.
cp ../../python/MQTTClient.py python/.
cp ../../python/IComputable.py python/.
cp ../../python/parse_cmd.py python/.

PROTO_PATH="../../"
PROTO_FILES=$(find "${r"${PROTO_PATH}"}" -name "*.proto")
if [ -n "${r"${PROTO_FILES}"}" ]
then
  echo "compiling .proto files:"
  echo "${r"${PROTO_FILES}"}"
  find "${r"${PROTO_PATH}"}" -name "*.proto" -print0 | xargs -0 protoc --python_out=hwc/. --proto_path="${r"${PROTO_PATH}"}"
fi
echo "Copy all hwc python-code to python directory" # This avoids directory based clashes with imports
cp -n `find hwc/* -name "*.py"` python/.

</#if>
<#if splittingModeIsLocal>
cp -r ../../"$COMPNAME"/ports .
</#if>
<#if replayEnabled>
cp ../../../../recordings.json .
</#if>

chmod +x *.sh
cd ../..
</#if>

cd "$CALLER_PWD" > /dev/null

