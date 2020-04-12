// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend.util

import java.util.Collection
import java.util.HashSet
import montiarc._symboltable.PortSymbol
import montithings.generator.helper.ComponentHelper
import montithings._symboltable.ComponentSymbol
import montithings._symboltable.ResourcePortSymbol
import montithings.generator.codegen.xtend.util.Utils

class Ports {

  def static String printIncludes(ComponentSymbol comp) {
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
	
	def static printVars(ComponentSymbol comp, Collection<PortSymbol> ports) {
	return	'''
	  // Ports
    «FOR port : ports»
    «var type = ComponentHelper.getRealPortCppTypeString(port.component.get, port)»
    «var name = port.name»
    Port<«type»>* «name» = new Port<«type»>;
    «ENDFOR»
	// Internal monitoring of ports
    «FOR port : ports»
    «var name = port.name»
    «IF comp.isDecomposed»
    sole::uuid portMonitorUuid«name.toFirstUpper» = sole::uuid4 ();
    «ENDIF»
    «ENDFOR»
    '''
		
	}
	
	def static printResourcePortVars(Collection<ResourcePortSymbol> ports) {
	return	'''
«FOR port : ports»
«var type = ComponentHelper.getResourcePortType(port)»
«var name = port.name»
«IF (port.ipc && !port.outgoing)»
Port<«type»>* «name» = new IPCPort<«type»>(IN, "«port.uri»");
«ENDIF»
«IF (port.ipc && port.outgoing)»
Port<«type»>* «name» = new IPCPort<«type»>(OUT, "«port.uri»");
«ENDIF»
«IF (port.webSocket && !port.outgoing)»
Port<«type»>* «name» = new WSPort<«type»>(IN, "«port.uri»");
«ENDIF»
«IF (port.webSocket && port.outgoing)»
Port<«type»>* «name» = new WSPort<«type»>(OUT, "«port.uri»");
«ENDIF»
«ENDFOR»
    '''
		
	}
	
	def static printResourcePortMethodHeaders(Collection<ResourcePortSymbol> ports){
	return	'''
    «FOR port : ports»
    «var type = ComponentHelper.getResourcePortType(port)»
    «var name = port.name»
    Port<«type»>* getPort«name.toFirstUpper»();
    void setPort«name.toFirstUpper»(Port<«type»>* «name»);
    «ENDFOR»
    '''
	}
	
		def static printResourcePortMethodBodies(Collection<ResourcePortSymbol> ports, ComponentSymbol comp, String compname){
	return	'''
    «FOR port : ports»
    «var type = ComponentHelper.getResourcePortType(port)»
    «var name = port.name»
    «Utils.printTemplateArguments(comp)»
    Port<«type»>* «compname»«Utils.printFormalTypeParameters(comp)»::getPort«name.toFirstUpper»(){
    	return «name»;
    }
	
    «Utils.printTemplateArguments(comp)»
    void «compname»«Utils.printFormalTypeParameters(comp)»::setPort«name.toFirstUpper»(Port<«type»>* port){
    	«name» = port;
    }
    «ENDFOR»
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
	
	def static printMethodBodies(Collection<PortSymbol> ports, ComponentSymbol comp, String compname){
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
«««    	«ENDIF»
    	«name» = port;
    }
    
    «ENDFOR»
    '''
	}
	

	
	
	
}