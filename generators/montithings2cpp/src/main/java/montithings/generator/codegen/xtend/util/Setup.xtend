// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend.util

import arcbasis._ast.ASTConnector
import arcbasis._ast.ASTPortAccess
import arcbasis._symboltable.ComponentTypeSymbol
import montithings._ast.ASTMTComponentType

class Setup {
	
	def static print(ComponentTypeSymbol comp, String compname) {
		if (comp.isAtomic) {
      return printSetupAtomic(comp, compname)
    } else {
      return printSetupComposed(comp, compname)
    }

	}
	
	def static printSetupAtomic(ComponentTypeSymbol comp, String compname) {
		return '''
		«Utils.printTemplateArguments(comp)»
		void «compname»«Utils.printFormalTypeParameters(comp, false)»::setUp(TimeMode enclosingComponentTiming){
			if (enclosingComponentTiming == TIMESYNC) {timeMode = TIMESYNC;}
			«IF comp.presentParentComponent»
			super.setUp(enclosingComponentTiming);
			«ENDIF»
			initialize();
		}
		'''
	}
	
	def static printSetupComposed(ComponentTypeSymbol comp, String compname) {
		return '''
		«Utils.printTemplateArguments(comp)»
		void «compname»«Utils.printFormalTypeParameters(comp, false)»::setUp(TimeMode enclosingComponentTiming){
			if (enclosingComponentTiming == TIMESYNC) {timeMode = TIMESYNC;}
			«IF comp.presentParentComponent»
			super.setUp(enclosingComponentTiming);
			«ENDIF» TODO
			«FOR subcomponent : comp.subComponents»
			«subcomponent.name».setUp(enclosingComponentTiming);
	        «ENDFOR»
		
		«FOR ASTConnector connector : (comp.getAstNode() as ASTMTComponentType).getConnectors()»
		          «FOR ASTPortAccess target : connector.targetList»
		            «IF !target.portSymbol.isIncoming»
		            // implements "connect «connector»"
		            «target.component»«IF target.component.equals("this")»->«ELSE».«ENDIF»getPort«target.port.toFirstUpper»()->setDataProvidingPort («connector.source.component»«IF connector.source.component.equals("this")»->«ELSE».«ENDIF»getPort«connector.source.port.toFirstUpper»());
		            «ENDIF»
		          «ENDFOR»
		«ENDFOR»
		}
		'''
	}
	
}