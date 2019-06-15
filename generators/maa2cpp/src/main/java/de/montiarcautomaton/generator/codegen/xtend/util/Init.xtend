package de.montiarcautomaton.generator.codegen.xtend.util

import montiarc._ast.ASTConnector
import de.montiarcautomaton.generator.helper.ComponentHelper
import montiarc._symboltable.ComponentSymbol
import montiarc._ast.ASTComponent
import de.monticore.types.types._ast.ASTQualifiedName

class Init {
	def static print(ComponentSymbol comp) {
    if (comp.isAtomic) {
    	return printInitAtomic(comp)
    } else {
      return printInitComposed(comp)
    }
  }
	
	def static printInitAtomic(ComponentSymbol comp) {
		return '''
		void «comp.name»::init(){
			«IF comp.superComponent.present»
			super.init();
		    «ENDIF»
		 
		   
		}    
		'''
	}
	
	def static printInitComposed(ComponentSymbol comp) {
		var helper = new ComponentHelper(comp);
		return '''
		void «comp.name»::init(){
		«IF comp.superComponent.present»
			super.init();
		    «ENDIF»
			
		«FOR ASTConnector connector : (comp.getAstNode().get() as ASTComponent)
		          .getConnectors()»
		          «FOR ASTQualifiedName target : connector.targetsList»
		            «IF helper.isIncomingPort(comp, connector.source, target, false)»
		              «helper.getConnectorComponentName(connector.source, target, false)».setPort«helper.getConnectorPortName(connector.source, target, false).toFirstUpper»(«helper.getConnectorComponentName(connector.source, target,true)».getPort«helper.getConnectorPortName(connector.source, target, true).toFirstUpper»());
		            «ENDIF»
		          «ENDFOR»
		    «ENDFOR» 
		    
		    «FOR subcomponent : comp.subComponents»
    		«subcomponent.name».init();
            «ENDFOR» 


			
		}
		'''
	}
	
}