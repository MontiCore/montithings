// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend.util

import arcbasis._ast.ASTConnector
import arcbasis._ast.ASTPortAccess
import arcbasis._symboltable.ComponentTypeSymbol
import montithings._ast.ASTMTComponentType
import montithings.generator.helper.ComponentHelper
import montithings.generator.codegen.xtend.util.Utils

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
			«IF ComponentHelper.isIncomingPort(comp, target)»
				// implements "connect «connector.source» -> «target»"
				«Utils.printGetPort(target)»->setDataProvidingPort («Utils.printGetPort(connector.source)»);
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