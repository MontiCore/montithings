// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend.util

import java.util.List
import montithings.generator.helper.ComponentHelper
import arcbasis._symboltable.ComponentTypeSymbol
import org.eclipse.xtext.xbase.lib.Pair

class Scripts {
	
	def static printRunScript(ComponentTypeSymbol comp, List<String> components) {
        var instances = ComponentHelper.getInstances(comp);
		return '''
		«FOR pair : instances»
		./«pair.getKey().fullName» «pair.getValue()» > «pair.getValue()».log 2>&1 &
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

    def static printBuildScript() {
        return '''
        if [ "$#" -ne 1 ] 
		then
			echo "Please provide the component you want to run as first argument. Aborting."
			exit 1
		fi
		mkdir build
		cd build
		cmake -G Ninja ..
		ninja
		cd bin
		cp ../../"$1"/*.sh .
		chmod +x *.sh
		cd ../..
		'''
    }
}