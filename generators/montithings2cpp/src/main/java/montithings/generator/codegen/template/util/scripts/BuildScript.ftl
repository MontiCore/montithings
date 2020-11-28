<#-- (c) https://github.com/MontiCore/monticore -->
#!/bin/sh
${tc.signature("config")}

set -e # Stop on first error

if [ "$#" -ne 1 ]
then
echo "Please provide the component you want to run as first argument. Aborting."
exit 1
fi

if [[ ! -d "$1" ]]
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
    <#if config.getTargetPlatform().toString() == "DSA_LAB">
      $CMAKE -G Ninja ..
    <#else>
      cmake -G Ninja ..
    </#if>
  ninja
</#if>

<#if config.getSplittingMode().toString() != "OFF">
echo Copy Scripts for "$1"
cd bin
cp ../../"$1"/*.sh .
<#if config.getMessageBroker().toString() == "DDS">
cp ../../"$1"/*.ini .
</#if>
cp -r ../../"$1"/ports .
chmod +x *.sh
cd ../..
</#if>