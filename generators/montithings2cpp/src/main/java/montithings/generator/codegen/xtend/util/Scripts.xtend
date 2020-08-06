// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend.util

import java.util.List

class Scripts {
	
	def static printRunScript(List<String> components) {
		return '''
		«FOR comp : components»
		./«comp» > «comp».log &
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
}