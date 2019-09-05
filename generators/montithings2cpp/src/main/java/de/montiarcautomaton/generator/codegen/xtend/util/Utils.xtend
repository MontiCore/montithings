/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 * 
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator.codegen.xtend.util

import de.montiarcautomaton.generator.helper.ComponentHelper
import de.monticore.types.types._ast.ASTTypeVariableDeclaration
import java.util.ArrayList
import java.util.List
import montiarc._ast.ASTComponent
import montiarc._ast.ASTVariableDeclaration
import montiarc._symboltable.ComponentSymbol
import de.monticore.ast.ASTNode
import montithings._symboltable.ResourcePortSymbol

class Utils {

  /**
   * Prints the component's configuration parameters as a comma separated list.
   */
  def static printConfiurationParametersAsList(ComponentSymbol comp) {
    var helper = new ComponentHelper(comp)
    return '''
      «FOR param : comp.configParameters SEPARATOR ','» «helper.printFqnTypeName(comp.astNode.get as ASTComponent, param.type)» «param.name» «ENDFOR»
    '''
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
        «printMember(ComponentHelper.printTypeName(param.type), param.name, "private final")»
      «ENDFOR»
    '''
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
   * Prints formal parameters of a component.
   */
  def static printFormalTypeParameters(ComponentSymbol comp) {
    return '''
      «IF (comp.astNode.get as ASTComponent).head.isPresentGenericTypeParameters»
        <
          «FOR generic : getGenericParameters(comp) SEPARATOR ','»
            «generic»
          «ENDFOR»
        >
      «ENDIF»
    '''
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
		«IF symbol.incoming»
		    «type» getData() override;
		«ELSE»
			void processData(«type» data) override;
		«ENDIF»
		public:
		    «symbol.name.toFirstUpper»Server(const char *uri) : AbstractIPC«IF symbol.incoming»Server«ELSE»Client«ENDIF»(uri){};
		};
		'''
	}
	
	def static printIPCServerBody(ResourcePortSymbol port, ComponentSymbol comp, Boolean existsHWC){
		var type = ComponentHelper.getResourcePortType(port)
		return 
		'''
		#include "«port.name.toFirstUpper»Server.h"
		
		«IF !existsHWC»
		«IF port.incoming»
		«type» «port.name.toFirstUpper»Server::getData(){
			//ToDo: FillMe
			throw std::runtime_error("Invoking getData() on empty implementation");
		}
		«ELSE»
		void «port.name.toFirstUpper»Server::processData(«type» data){
			//ToDo: FillMe
			throw std::runtime_error("Invoking processData() on empty implementation");
		}
		«ENDIF»
		«ENDIF»
		int
		main(int argc, char **argv) try {
		    auto server = «port.name.toFirstUpper»Server("«port.uri»");
		    server.run();
		    return 1;
		} catch (const nng::exception &e) {
		    fprintf(stderr, "%s: %s\n", e.who(), e.what());
		    return 1;
		}
		'''
	}

}
