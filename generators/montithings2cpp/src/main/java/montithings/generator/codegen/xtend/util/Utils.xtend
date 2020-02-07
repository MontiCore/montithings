// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend.util

import montithings.generator.helper.ComponentHelper
import de.monticore.types.types._ast.ASTTypeVariableDeclaration
import java.util.ArrayList
import java.util.List
import montiarc._ast.ASTComponent
import montiarc._ast.ASTVariableDeclaration
import montiarc._symboltable.ComponentSymbol
import montithings._symboltable.ResourcePortSymbol

class Utils {

  /**
   * Prints the component's configuration parameters as a comma separated list.
   */
  def static printConfigurationParametersAsList(ComponentSymbol comp) {
    var helper = new ComponentHelper(comp)
    return '''
      «FOR param : comp.configParameters SEPARATOR ','» «helper.printParamTypeName(comp.astNode.get as ASTComponent, param.type)» «param.name» «ENDFOR»
    '''.toString().replace("\n", "")
  }

  /**
   * Prints the component's imports
   */
  def static printImports(ComponentSymbol comp) {
    return '''
      «FOR _import : comp.imports»
        import «_import.statement»«IF _import.isStar».*«ENDIF»;
      «ENDFOR»
      «FOR inner : comp.innerComponents»
        import «printPackageWithoutKeyWordAndSemicolon(inner) + "." + inner.name»;
      «ENDFOR»
    '''
  }

  /**
   * Prints a member of given visibility name and type
   */
  def static printMember(String type, String name, String visibility) {
    return '''
      «type» «name»;
    '''
  }
  
  /**
   * Prints members for configuration parameters.
   */
  def static printConfigParameters(ComponentSymbol comp) {
    return '''
      «FOR param : (comp.astNode.get as ASTComponent).head.parameterList»
        «printMember(ComponentHelper.printCPPTypeName(param.type), param.name, "")»
      «ENDFOR»
    '''.toString().replace("\n", "")
  }

  /**
   * Prints members for variables
   */
  def static printVariables(ComponentSymbol comp) {
    return '''
      «FOR variable : comp.variables»
        «printMember(ComponentHelper.printCPPTypeName((variable.astNode.get as ASTVariableDeclaration).type), variable.name, "")»
      «ENDFOR»
    '''
  }

  /**
   * Check if a component is generic
   */
  def static Boolean hasTypeParameters(ComponentSymbol comp) {
    return (comp.astNode.get as ASTComponent).head.isPresentGenericTypeParameters;
  }

  /**
   * Prints formal parameters of a component.
   */
  def static printFormalTypeParameters(ComponentSymbol comp) {
    return printFormalTypeParameters(comp, false)
  }
  def static printFormalTypeParameters(ComponentSymbol comp, Boolean withClassPrefix) {
    return '''
      «IF hasTypeParameters(comp)»
        <
          «FOR generic : getGenericParameters(comp) SEPARATOR ','»
            «IF withClassPrefix»class «ENDIF»«generic»
          «ENDFOR»
        >
      «ENDIF»
    '''.toString().replace("\n", "")
  }

  def static String printTemplateArguments(ComponentSymbol comp) {
    return '''
    «IF Utils.hasTypeParameters(comp)»
      template«Utils.printFormalTypeParameters(comp, true)»
    «ENDIF»
    '''.toString().replace("\n", "")
  }

  def private static List<String> getGenericParameters(ComponentSymbol comp) {
    var componentNode = comp.astNode.get as ASTComponent
    var List<String> output = new ArrayList
    if (componentNode.getHead().isPresentGenericTypeParameters()) {
      var List<ASTTypeVariableDeclaration> parameterList = componentNode.getHead().getGenericTypeParameters().
        getTypeVariableDeclarationList()
      for (ASTTypeVariableDeclaration variableDeclaration : parameterList) {
        output.add(variableDeclaration.getName())
      }
    }
    return output;
  }
  
  /**
   * Print the package declaration for generated component classes.
   * Uses recursive determination of the package name to accomodate for components
   * with at least two levels of inner component. These require changing the package name
   * to avoid name clashes between the generated packages and the outermost component.
   */
  def static String printPackage(ComponentSymbol comp) {
  	return '''
  	«IF comp.isInnerComponent»
  	package «printPackageWithoutKeyWordAndSemicolon(comp.definingComponent.get) + "." + comp.definingComponent.get.name + "gen"»;
	«ELSE»
  	package «comp.packageName»;
	«ENDIF»
  	'''
  }
  
  /**
   * Helper function used to determine package names.
   */
  def static String printPackageWithoutKeyWordAndSemicolon(ComponentSymbol comp){
  	return '''
  	«IF comp.isInnerComponent»
  	«printPackageWithoutKeyWordAndSemicolon(comp.definingComponent.get) + "." + comp.definingComponent.get.name + "gen"»
	«ELSE»
  	«comp.packageName»
	«ENDIF»
  	'''
  }
  
  def static String printSuperClassFQ(ComponentSymbol comp){
  	var String packageName = printPackageWithoutKeyWordAndSemicolon(comp.superComponent.get.referencedSymbol);
  	if(packageName.equals("")){
  		return '''«comp.superComponent.get.name»'''
  	} else {
  		return '''«packageName».«comp.superComponent.get.name»'''
  	}
  }
  
  def static String printCPPImports(ComponentSymbol comp){
  	return '''
  	
  	«FOR importString : ComponentHelper.getCPPImports(comp)»
  	#include "«importString»"
  	«ENDFOR»
  	'''
  }
	
	def static printIPCServerHeader(ResourcePortSymbol symbol, ComponentSymbol comp) {
		var type = ComponentHelper.getResourcePortType(symbol)
		return
		'''
		#pragma once
		#include "IComponent.h"
		#include "Port.h"
		#include <string>
		#include <map>
		#include <vector>
		#include <list>
		#include <set>
		#include "IncomingIPCPort.h"
		#include "OutgoingIPCPort.h"
		«Utils.printCPPImports(comp)»
		#include <AbstractIPC«IF symbol.incoming»Server«ELSE»Client«ENDIF».h>
		
		class «symbol.name.toFirstUpper»Server : public AbstractIPC«IF symbol.incoming»Server«ELSE»Client«ENDIF»<«type»>{
		private:
		«IF symbol.resourceParameters.size > 0»
		map<std::string,std::string> parameters = 
			{ «FOR parameter : symbol.resourceParameters SEPARATOR ','»{"«parameter.key»","«parameter.value»"}«ENDFOR» };
		«ENDIF»
		«IF symbol.incoming»
		    «type» getData() override;
		«ELSE»
			void processData(«type» data) override;
		«ENDIF»

		public:
		    «symbol.name.toFirstUpper»Server(const char *uri) : AbstractIPC«IF symbol.incoming»Server«ELSE»Client«ENDIF»(uri){};
		    void setup();
		};
		'''
	}
	
	def static printIPCServerBody(ResourcePortSymbol port, ComponentSymbol comp, Boolean existsHWC){
		var type = ComponentHelper.getResourcePortType(port)
		return 
		'''
		#include "«port.name.toFirstUpper»Server.h"

		void «port.name.toFirstUpper»Server::setup(){
		 //ToDo: Fill Me if needed
		}

		«IF !existsHWC»
		«IF port.incoming»
		«type» «port.name.toFirstUpper»Server::getData(){
			//ToDo: Fill Me
			throw std::runtime_error("Invoking getData() on empty implementation");
		}
		«ELSE»
		void «port.name.toFirstUpper»Server::processData(«type» data){
			//ToDo: Fill Me
			throw std::runtime_error("Invoking processData() on empty implementation");
		}
		«ENDIF»
		«ENDIF»
		int
		main(int argc, char **argv) try {
		    auto server = «port.name.toFirstUpper»Server("«port.uri»");
		    server.setup();
		    server.run();
		    return 1;
		} catch (const nng::exception &e) {
		    fprintf(stderr, "%s: %s\n", e.who(), e.what());
		    return 1;
		}
		'''
	}

}
