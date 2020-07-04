// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend.util

import arcbasis._ast.ASTConnector
import arcbasis._ast.ASTPortAccess
import arcbasis._symboltable.ComponentTypeSymbol
import montithings._ast.ASTMTComponentType

class Init {
	def static print(ComponentTypeSymbol comp, String compname) {
    if (comp.isAtomic) {
    	return printInitAtomic(comp, compname)
    } else {
      return printInitComposed(comp, compname)
    }
  }
	
	def static printInitAtomic(ComponentTypeSymbol comp, String compname) {
		return '''
		«Utils.printTemplateArguments(comp)»
		void «compname»«Utils.printFormalTypeParameters(comp, false)»::init(){
			«IF comp.presentParentComponent»
			super.init();
		    «ENDIF»
		 
		   
		}    
		'''
	}
	
	def static printInitComposed(ComponentTypeSymbol comp, String compname) {
		return '''
		«Utils.printTemplateArguments(comp)»
		void «compname»«Utils.printFormalTypeParameters(comp, false)»::init(){
		«IF comp.presentParentComponent»
			super.init();
		    «ENDIF»
			
		«FOR ASTConnector connector : (comp.getAstNode() as ASTMTComponentType).getConnectors()»
		          «FOR ASTPortAccess target : connector.targetList»
		            «IF target.portSymbol.isIncoming»
		            	// implements "connect «connector»"
                  	    «target.component»«IF target.component.equals("this")»->«ELSE».«ENDIF»getPort«target.port.toFirstUpper» ()->setDataProvidingPort(«connector.source.component»«IF connector.source.component.equals("this")»->«ELSE».«ENDIF»getPort«connector.source.port.toFirstUpper» ());
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