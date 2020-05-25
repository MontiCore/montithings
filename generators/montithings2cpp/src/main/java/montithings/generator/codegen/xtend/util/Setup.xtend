// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend.util

import montithings._symboltable.ComponentSymbol
import montiarc._ast.ASTConnector
import montiarc._ast.ASTComponent
import de.monticore.types.types._ast.ASTQualifiedName
import montithings.generator.helper.ComponentHelper
import montithings.generator.codegen.xtend.util.Utils

class Setup {
	
	def static print(ComponentSymbol comp, String compname) {
		if (comp.isAtomic) {
      return printSetupAtomic(comp, compname)
    } else {
      return printSetupComposed(comp, compname)
    }

	}
	
	def static printSetupAtomic(ComponentSymbol comp, String compname) {
		return '''
		«Utils.printTemplateArguments(comp)»
		void «compname»«Utils.printFormalTypeParameters(comp, false)»::setUp(TimeMode enclosingComponentTiming){
			if (enclosingComponentTiming == TIMESYNC) {timeMode = TIMESYNC;}
			«IF comp.superComponent.present»
			super.setUp(enclosingComponentTiming);
			«ENDIF»
			initialize();
		}
		'''
	}
	
	def static printSetupComposed(ComponentSymbol comp, String compname) {
		var helper = new ComponentHelper(comp)
		return '''
		«Utils.printTemplateArguments(comp)»
		void «compname»«Utils.printFormalTypeParameters(comp, false)»::setUp(TimeMode enclosingComponentTiming){
			if (enclosingComponentTiming == TIMESYNC) {timeMode = TIMESYNC;}
			«IF comp.superComponent.present»
			super.setUp(enclosingComponentTiming);
			«ENDIF»
			«FOR subcomponent : comp.subComponents»
			«subcomponent.name».setUp(enclosingComponentTiming);
	        «ENDFOR»

	        «FOR ASTConnector connector : (comp.getAstNode().get() as ASTComponent).getConnectors()»
              «FOR ASTQualifiedName target : connector.targetsList»
                «IF !helper.isIncomingPort(comp,connector.source, target, false)»
            // implements "connect «connector»"
				    «helper.getConnectorComponentName(connector.source, target,false)»«IF helper.getConnectorComponentName(connector.source, target,false).equals("this")»->«ELSE».«ENDIF»getPort«helper.getConnectorPortName(connector.source, target,false).toFirstUpper»()->setDataProvidingPort («helper.getConnectorComponentName(connector.source, target, true)»«IF helper.getConnectorComponentName(connector.source, target,true).equals("this")»->«ELSE».«ENDIF»getPort«helper.getConnectorPortName(connector.source, target, true).toFirstUpper»());
                «ENDIF»
              «ENDFOR»
            «ENDFOR»
		}
		'''
	}
	
}