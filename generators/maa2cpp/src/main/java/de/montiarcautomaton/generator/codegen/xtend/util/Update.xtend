package de.montiarcautomaton.generator.codegen.xtend.util

import montiarc._symboltable.ComponentSymbol

class Update {
	def static print(ComponentSymbol comp) {
    if (comp.isDecomposed) {
    	return printUpdateComposed(comp)
    } else {
    	return printUpdateAtomic(comp)
    }
  }
	
	def static printUpdateComposed(ComponentSymbol comp) {
		return '''
		void «comp.name»::update(){
			«IF comp.superComponent.present»
	        super.update();
	      	«ENDIF»
	      	«FOR subcomponent : comp.subComponents»
  	        «subcomponent.name».update();
  	      	«ENDFOR»

		}
		
		
		'''
	}
	
	def static printUpdateAtomic(ComponentSymbol comp) {
		return '''
		void «comp.name»::update(){
			«IF comp.superComponent.present»
	        super.update();
	      	«ENDIF»
	      	«FOR subcomponent : comp.subComponents»
  	        «subcomponent.name».update();
  	      	«ENDFOR»
	  	    «FOR portOut : comp.outgoingPorts»
	  	    «portOut.name»->update();
	  	    «ENDFOR»

		}
		
		
		'''
	}
	
}