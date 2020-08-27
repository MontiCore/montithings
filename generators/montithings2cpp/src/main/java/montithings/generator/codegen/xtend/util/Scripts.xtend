// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend.util

import java.util.List
import montithings.generator.helper.ComponentHelper
import arcbasis._symboltable.ComponentTypeSymbol
import montithings.generator.codegen.ConfigParams

class Scripts {
  
  def static printRunScript(ComponentTypeSymbol comp, ConfigParams config) {
        var instances = ComponentHelper.getInstances(comp);
    return '''
    «FOR pair : instances»
    ./«pair.getKey().fullName» «pair.getValue()» «config.componentPortMap.getManagementPort(pair.getValue())» «config.componentPortMap.getCommunicationPort(pair.getValue())» > «pair.getValue()».log 2>&1 &
    «ENDFOR»
    '''
  }

    def static printKillScript(List<String> components) {
    return '''
    «FOR comp : components»
    killall «comp»
    «ENDFOR»
    '''
  }

    def static printBuildScript(ConfigParams config) {
        return '''
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
    «IF config.getTargetPlatform() == ConfigParams.TargetPlatform.DSA_VCG»
    dev-docker.sh l06 build
    cd build_dev-l06_*
    «ELSE»
    mkdir build
    cd build
    cmake -G Ninja ..
    ninja
    «ENDIF»
    echo Copy Scripts for "$1"
    cd bin
    cp ../../"$1"/*.sh .
    cp -r ../../"$1"/ports .
    chmod +x *.sh
    cd ../..
    '''
    }
}