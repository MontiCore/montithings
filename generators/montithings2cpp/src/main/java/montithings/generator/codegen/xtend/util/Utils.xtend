// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend.util

import montithings.generator.helper.ComponentHelper
import de.monticore.types.types._ast.ASTTypeVariableDeclaration
import java.util.ArrayList
import java.util.List
import montithings._ast.ASTMTComponentType
import arcbasis._ast.ASTVariableDeclaration
import arcbasis._symboltable.ComponentTypeSymbol
import montithings._symboltable.ResourcePortSymbol
import de.monticore.mcexpressions._ast.ASTExpression
import de.monticore.prettyprint.IndentPrinter
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter
import montithings.generator.visitor.CDAttributeGetterTransformationVisitor

class Utils {

  /**
   * Prints the component's configuration parameters as a comma separated list.
   */
  def static printConfigurationParametersAsList(ComponentTypeSymbol comp) {
    var helper = new ComponentHelper(comp)
    return '''
      «FOR param : comp.configParameters SEPARATOR ','» «helper.printParamTypeName(comp.astNode as ASTMTComponentType, param.type)» «param.name» «ENDFOR»
    '''.toString().replace("\n", "")
  }

  /**
   * Prints the component's imports
   */
  def static printImports(ComponentTypeSymbol comp) {
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
  def static printConfigParameters(ComponentTypeSymbol comp) {
    return '''
      «FOR param : (comp.astNode as ASTMTComponentType).head.parameterList»
        «printMember(ComponentHelper.printCPPTypeName(param.type), param.name, "")»
      «ENDFOR»
    '''.toString().replace("\n", "")
  }

  /**
   * Prints members for variables
   */
  def static printVariables(ComponentTypeSymbol comp) {
    return '''
      «FOR variable : comp.variables»
        «printMember(ComponentHelper.printCPPTypeName((variable.astNode as ASTVariableDeclaration).type), variable.name, "")»
      «ENDFOR»
    '''
  }

  /**
   * Check if a component is generic
   */
  def static Boolean hasTypeParameters(ComponentTypeSymbol comp) {
    return (comp.astNode as ASTMTComponentType).head.isPresentGenericTypeParameters;
  }

  /**
   * Prints formal parameters of a component.
   */
  def static printFormalTypeParameters(ComponentTypeSymbol comp) {
    return printFormalTypeParameters(comp, false)
  }
  def static printFormalTypeParameters(ComponentTypeSymbol comp, Boolean withClassPrefix) {
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

  def static String printTemplateArguments(ComponentTypeSymbol comp) {
    return '''
    «IF Utils.hasTypeParameters(comp)»
      template«Utils.printFormalTypeParameters(comp, true)»
    «ENDIF»
    '''.toString().replace("\n", "")
  }

  def private static List<String> getGenericParameters(ComponentTypeSymbol comp) {
    var componentNode = comp.astNode as ASTMTComponentType
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
  def static String printPackage(ComponentTypeSymbol comp) {
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
  def static String printPackageWithoutKeyWordAndSemicolon(arcbasis._symboltable.ComponentTypeSymbol comp){
  	return '''
  	«IF comp.isInnerComponent»
  	«printPackageWithoutKeyWordAndSemicolon(comp.definingComponent.get) + "." + comp.definingComponent.get.name + "gen"»
	«ELSE»
  	«comp.packageName»
	«ENDIF»
  	'''
  }
  
  def static String printSuperClassFQ(ComponentTypeSymbol comp){
  	var String packageName = printPackageWithoutKeyWordAndSemicolon(comp.parentInfo);
  	if(packageName.equals("")){
  		return '''«comp.parent.name»'''
  	} else {
  		return '''«packageName».«comp.parent.name»'''
  	}
  }
  
  def static String printNamespaceStart(ComponentTypeSymbol comp) {
  	var packages = ComponentHelper.getPackages(comp);
  	return '''
  	namespace montithings {
  	«FOR i : 0..<packages.size»
  	namespace «packages.get(i)» {
  	«ENDFOR»
  	'''
  }
  
  def static String printNamespaceEnd(ComponentTypeSymbol comp) {
  	var packages = ComponentHelper.getPackages(comp);
  	return '''
	«FOR i : 0..<packages.size»
	} // namespace «packages.get(packages.size - (i+1))»
	«ENDFOR»
	} // namespace montithings
  	'''
  }
  
  def static String printCPPImports(ComponentTypeSymbol comp){
  	return '''
  	
  	«FOR importString : ComponentHelper.getCPPImports(comp)»
  	#include "«importString»"
  	«ENDFOR»
  	'''
  }
	
	def static printIPCServerHeader(ResourcePortSymbol symbol, ComponentTypeSymbol comp) {
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
		«Utils.printCPPImports(comp)»
		#include <AbstractIPC«IF symbol.incoming»Server«ELSE»Client«ENDIF».h>
		
		«Utils.printNamespaceStart(comp)»
		
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
		«Utils.printNamespaceEnd(comp)»
		'''
	}
	
	def static printIPCServerBody(ResourcePortSymbol port, ComponentTypeSymbol comp, Boolean existsHWC){
		var type = ComponentHelper.getResourcePortType(port)
		return 
		'''
		#include "«port.name.toFirstUpper»Server.h"
		
		«Utils.printNamespaceStart(comp)»

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
		«Utils.printNamespaceEnd(comp)»
		
		int
		main(int argc, char **argv) try {
		    auto server = «ComponentHelper.printPackageNamespaceForComponent(comp)»«port.name.toFirstUpper»Server("«port.uri»");
		    server.setup();
		    server.run();
		    return 1;
		} catch (const nng::exception &e) {
		    fprintf(stderr, "%s: %s\n", e.who(), e.what());
		    return 1;
		}
		'''
	}
	
	def static String printExpression(ASTExpression expr, boolean isAssignment) {
    	var IndentPrinter printer = new IndentPrinter();
    	var JavaDSLPrettyPrinter prettyPrinter = new JavaDSLPrettyPrinter(printer);
    	if (isAssignment) {
      		prettyPrinter = new CDAttributeGetterTransformationVisitor(printer);
    	}
    	expr.accept(prettyPrinter);
    	return printer.getContent();
  	}

	def static String printExpression(ASTExpression expr) {
    	return printExpression(expr, true);
	}

}
