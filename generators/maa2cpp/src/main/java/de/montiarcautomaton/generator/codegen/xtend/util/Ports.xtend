package de.montiarcautomaton.generator.codegen.xtend.util

import java.util.Collection
import montiarc._symboltable.PortSymbol
import de.montiarcautomaton.generator.helper.ComponentHelper
import montiarc._symboltable.ComponentSymbol

class Ports {
	
	def static printVars(Collection<PortSymbol> ports) {
	return	'''
    «FOR port : ports»
    «var type = ComponentHelper.getRealPortTypeString(port.component.get, port)»
    «var name = port.name»
    
   Port<«type»>* «name» = new Port<«type»>;
    
    «ENDFOR»
    '''
		
	}
	
	def static printMethodHeaders(Collection<PortSymbol> ports){
	return	'''
    «FOR port : ports»
    «var type = ComponentHelper.getRealPortTypeString(port.component.get, port)»
    «var name = port.name»
    
    
    Port<«type»>* getPort«name.toFirstUpper»();
    void setPort«name.toFirstUpper»(Port<«type»>* «name»);
    
    «ENDFOR»
    '''
	}
	
	def static printMethodBodies(Collection<PortSymbol> ports, ComponentSymbol comp){
	return	'''
    «FOR port : ports»
    «var type = ComponentHelper.getRealPortTypeString(port.component.get, port)»
    «var name = port.name»
    
    
    Port<«type»>* «comp.name»::getPort«name.toFirstUpper»(){
    	return «name»;
    }
    
    void «comp.name»::setPort«name.toFirstUpper»(Port<«type»>* port){
    	«name» = port;
    }
    
    «ENDFOR»
    '''
	}
	
}