// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend.util

import java.util.Collection
import java.util.HashSet
import arcbasis._symboltable.PortSymbol
import montithings.generator.helper.ComponentHelper
import arcbasis._symboltable.ComponentTypeSymbol
import montithings.generator.codegen.xtend.util.Utils

class Ports {

  def static String printIncludes(ComponentTypeSymbol comp) {
  	var HashSet<String> portIncludes = new HashSet<String>()
    for (port : comp.ports) {
    	if (ComponentHelper.portUsesCdType(port)) {
    		var portNamespace = ComponentHelper.printCdPortPackageNamespace(comp, port)
      		portIncludes.add('''#include "«portNamespace.replace("::", "/")».h"''')
      	}
      }
	return '''
	«FOR include : portIncludes»
	«include»
	«ENDFOR»
	'''
  }
	
	def static printVars(ComponentTypeSymbol comp, Collection<PortSymbol> ports) {
	return	'''
	  // Ports
    «FOR port : ports»
    «var type = ComponentHelper.getRealPortCppTypeString(port.component.get, port)»
    «var name = port.name»
    Port<«type»>* «name» = new Port<«type»>;
    «ENDFOR»
    «IF comp.isDecomposed»
	// Internal monitoring of ports
    «FOR port : ports»
    «var name = port.name»
    sole::uuid portMonitorUuid«name.toFirstUpper» = sole::uuid4 ();
    «ENDFOR»
    «ENDIF»
    '''
		
	}
	
	def static printMethodHeaders(Collection<PortSymbol> ports){
	return	'''
    «FOR port : ports»
    «var type = ComponentHelper.getRealPortCppTypeString(port.component.get, port)»
    «var name = port.name»
    Port<«type»>* getPort«name.toFirstUpper»();
    void setPort«name.toFirstUpper»(Port<«type»>* «name»);
    «ENDFOR»
    '''
	}
	
	def static printMethodBodies(Collection<PortSymbol> ports, ComponentTypeSymbol comp, String compname){
	return	'''
    «FOR port : ports»
    «var type = ComponentHelper.getRealPortCppTypeString(port.component.get, port)»
    «var name = port.name»
    «Utils.printTemplateArguments(comp)»
    Port<«type»>* «compname»«Utils.printFormalTypeParameters(comp)»::getPort«name.toFirstUpper»(){
    	return «name»;
    }

    «Utils.printTemplateArguments(comp)»
    void «compname»«Utils.printFormalTypeParameters(comp)»::setPort«name.toFirstUpper»(Port<«type»>* port){
«««    	«IF comp.atomic»
«««    	port->registerPort(portUuid«name.toFirstUpper»);
«««    	«ENDIF»
«««    	«IF comp.isDecomposed»
«««    	port->registerPort(portMonitorUuid«name.toFirstUpper»);
«««    	«ENDIF» TDOD
    	«name» = port;
    }
    
    «ENDFOR»
    '''
    }
}