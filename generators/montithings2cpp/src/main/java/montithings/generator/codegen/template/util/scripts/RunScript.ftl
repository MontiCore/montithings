# (c) https://github.com/MontiCore/monticore
<#--package montithings.generator.codegen.xtend.util

import java.util.List
import montithings.generator.helper.ComponentHelper
import arcbasis._symboltable.ComponentTypeSymbol
import montithings.generator.codegen.ConfigParams-->

class Scripts {
  
  def static printRunScript(ComponentTypeSymbol comp, ConfigParams config) {
        <#assign instances = ComponentHelper.getInstances(comp);>
    return '''
    <#list instances as pair >
 ./${pair.getKey().fullName} ${pair.getValue()} ${config.componentPortMap.getManagementPort(pair.getValue())} ${config.componentPortMap.getCommunicationPort(pair.getValue())} > ${pair.getValue()}.log 2>&1 &
 </#list>
    '''
  }

    def static printBuildScript(ConfigParams config) {
        return '''
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
    <#if config.getTargetPlatform() == ConfigParams.TargetPlatform.DSA_VCG>
    dev-docker.sh l06 build
    cd build_dev-l06_*
    ${ELSE}
    mkdir -p build
    cd build
    <#if config.getTargetPlatform() == ConfigParams.TargetPlatform.DSA_LAB>
 $CMAKE -G Ninja ..
 <#else>
 cmake -G Ninja ..
  </#if>
    ninja
    </#if>
    echo Copy Scripts for "$1"
    cd bin
    cp ../../"$1"/*.sh .
    cp -r ../../"$1"/ports .
    chmod +x *.sh
    cd ../..
    '''
    }
}