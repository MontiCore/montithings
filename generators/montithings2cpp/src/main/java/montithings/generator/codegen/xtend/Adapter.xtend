// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend

import cdlangextension._ast.ASTCDEImportStatement
import java.util.HashSet
import java.util.List
import montithings.generator.codegen.ConfigParams
import montithings.generator.helper.ComponentHelper

class Adapter {

	def static generateCpp(List<String> packageName, String compname, ConfigParams config) {
    return '''
    #include "«compname»AdapterTOP.h"
    «printNamespaceStart(packageName)»
    «printNamespaceEnd(packageName)»
    '''
  }
	
	def static generateHeader(List<String> packageName, String compname, ConfigParams config) {
		return '''
#pragma once
#include <string>
#include "Port.h"
#include <map>
#include <vector>
#include <list>
#include <set>
#include <utility>
#include "tl/optional.hpp"
«printIncludes(ComponentHelper.getImportStatements(compname,config))»

«printNamespaceStart(packageName)»

class «compname»AdapterTOP
{
private:
public:
	«compname»AdapterTOP() = default;
	«FOR importStatement : ComponentHelper.getImportStatements(compname,config)»
	virtual «printCDType(importStatement)» convert(«importStatement.importClass» element) = 0;
	virtual «importStatement.importClass» convert(«printCDType(importStatement)» element) = 0;
	«ENDFOR»
};
«printNamespaceEnd(packageName)»
'''
	}
	
	def static String printIncludes(List<ASTCDEImportStatement> imports) {
  	var HashSet<String> portIncludes = new HashSet<String>()
    for (importStatement : imports) {
    	var portPackage = importStatement.getImportSource().toString();
    	portIncludes.add('''#include «portPackage»''');
      	portIncludes.add('''#include "«printCDType(importStatement).replace("::", "/")».h"''')
     }
	return '''
	«FOR include : portIncludes»
	«include»
	«ENDFOR»
	'''
  }
  
  	def static String printCDType(ASTCDEImportStatement importStatement) {
  		var namespace = new StringBuilder("montithings::");
    	if(importStatement.isPresentPackage()){
    	var packages = importStatement.getPackage().getPartList();
    	
    	for (String packageName : packages) {
      	namespace.append(packageName).append("::");
    	}
    	return ComponentHelper.printPackageNamespaceFromString(packages.get(packages.size-1)+"::"+importStatement.getName().toString(), namespace.toString());
    	}
        return importStatement.getName().toString();
  }

	def static String printNamespaceStart(List<String> packages) {
      	return '''
      	namespace montithings {
      	«FOR i : 0..<packages.size»
      	namespace «packages.get(i)» {
      	«ENDFOR»
      	'''
      }

      def static String printNamespaceEnd(List<String> packages) {
      	return '''
    	«FOR i : 0..<packages.size»
    	} // namespace «packages.get(packages.size - (i+1))»
    	«ENDFOR»
    	} // namespace montithings
      	'''
      }
}