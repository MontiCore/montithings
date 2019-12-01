package montithings.generator.codegen.xtend.util

import montiarc._ast.ASTConnector
import montithings.generator.helper.ComponentHelper
import montiarc._symboltable.ComponentSymbol
import montiarc._ast.ASTComponent
import de.monticore.types.types._ast.ASTQualifiedName

class Init {
	def static print(ComponentSymbol comp, String compname) {
    if (comp.isAtomic) {
    	return printInitAtomic(comp, compname)
    } else {
      return printInitComposed(comp, compname)
    }
  }
	
	def static printInitAtomic(ComponentSymbol comp, String compname) {
		return '''
		void «compname»::init(){
			«IF comp.superComponent.present»
			super.init();
		    «ENDIF»
		 
		   
		}    
		'''
	}
	
	def static printInitComposed(ComponentSymbol comp, String compname) {
		var helper = new ComponentHelper(comp);
		return '''
		void «compname»::init(){
		«IF comp.superComponent.present»
			super.init();
		    «ENDIF»
			
		«FOR ASTConnector connector : (comp.getAstNode().get() as ASTComponent)
		          .getConnectors()»
		          «FOR ASTQualifiedName target : connector.targetsList»
		            «IF helper.isIncomingPort(comp, connector.source, target, false)»
                  	    «helper.getConnectorComponentName(connector.source, target,false)»«IF helper.getConnectorComponentName(connector.source, target,false).equals("this")»->«ELSE».«ENDIF»setPort«helper.getConnectorPortName(connector.source, target,false).toFirstUpper»(«helper.getConnectorComponentName(connector.source, target, true)»«IF helper.getConnectorComponentName(connector.source, target,true).equals("this")»->«ELSE».«ENDIF»getPort«helper.getConnectorPortName(connector.source, target, true).toFirstUpper»());
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