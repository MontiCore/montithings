package de.montiarcautomaton.generator.codegen.xtend

import de.montiarcautomaton.generator.codegen.xtend.util.Utils
import de.montiarcautomaton.generator.helper.ComponentHelper
import montiarc._symboltable.ComponentSymbol

class Result {
	def static generateResultBody(ComponentSymbol comp){
		var ComponentHelper helper = new ComponentHelper(comp)
	    return '''
	    #include "«comp.name»Result.h"
	    
	    «IF !comp.allOutgoingPorts.empty»
	    «comp.name»Result::«comp.name»Result(«FOR port : comp.allOutgoingPorts SEPARATOR ','» «helper.getRealPortTypeString(port)» «port.name» «ENDFOR»){
	    	«IF comp.superComponent.present»
	    	      		  super(«FOR port : comp.superComponent.get.allOutgoingPorts» «port.name» «ENDFOR»);
	    	      		«ENDIF»
	    	      		«FOR port : comp.outgoingPorts»
	    	      		  this->«port.name» = «port.name»; 
	    	      		«ENDFOR»
	    }
	    «ENDIF»
	    
	    «FOR port : comp.outgoingPorts»
      	 «helper.getRealPortTypeString(port)» «comp.name»Result::get«port.name.toFirstUpper»(){
  	 	    return «port.name»;
      	 }
	     «ENDFOR»
	     
	     «FOR port : comp.outgoingPorts»
       	  void «comp.name»Result::set«port.name.toFirstUpper»(«helper.getRealPortTypeString(port)» «port.name»){
   	 	    this->«port.name» = «port.name»; 
       	 }
	     «ENDFOR»
	    
	    '''
	}
	
	def static generateResultHeader(ComponentSymbol comp){
	    var ComponentHelper helper = new ComponentHelper(comp)
	    return '''
	    #pragma once
	    using namespace std;
	    
	    class «comp.name»Result
	    			      «IF comp.superComponent.present» : 
	    			            «Utils.printSuperClassFQ(comp)»Result
	    			            «IF comp.superComponent.get.hasFormalTypeParameters»<
	    			            «FOR scTypeParams : helper.superCompActualTypeArguments SEPARATOR ','»
	    			                «scTypeParams»
	    			                «ENDFOR» > «ENDIF»
	    			            «ENDIF»
	    {
	    private:
	    	«FOR port : comp.outgoingPorts»
	    	«helper.getRealPortTypeString(port)» «port.name»;
	    	«ENDFOR»
	    
	    public:	
	    	«comp.name»Result() {};
	    	«IF !comp.allOutgoingPorts.empty»
	    	«comp.name»Result(«FOR port : comp.allOutgoingPorts SEPARATOR ','» «helper.getRealPortTypeString(port)» «port.name» «ENDFOR»);
	    	«ENDIF»
	    	
	    	«FOR port : comp.outgoingPorts»
	    	 «helper.getRealPortTypeString(port)» get«port.name.toFirstUpper»();
	    	«ENDFOR»
	    	
	    	«FOR port : comp.outgoingPorts»
	    	 void set«port.name.toFirstUpper»(«helper.getRealPortTypeString(port)» «port.name»);
	    	«ENDFOR»
	    };
	    '''
		
	}
}