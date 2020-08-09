// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend

import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.helper.ComponentHelper
import arcbasis._symboltable.ComponentTypeSymbol
import montithings.generator.codegen.xtend.util.Ports
import montithings.generator.codegen.ConfigParams

class Result {

  def static generateImplementationFile(ComponentTypeSymbol comp, String compname, ConfigParams config) {
    return '''
    #include "«compname»Result.h"
    «Utils.printNamespaceStart(comp)»
    «IF !comp.hasTypeParameter»
    «generateResultBody(comp, compname, config)»
    «ENDIF»
    «Utils.printNamespaceEnd(comp)»
    '''
  }


	def static generateResultBody(ComponentTypeSymbol comp, String compname, ConfigParams config){
		var ComponentHelper helper = new ComponentHelper(comp)
	    return '''
«IF !comp.allOutgoingPorts.empty»
«Utils.printTemplateArguments(comp)»
«compname»Result«Utils.printFormalTypeParameters(comp, false)»::«compname»Result(«FOR port : comp.allOutgoingPorts SEPARATOR ','» «helper.getRealPortCppTypeString(port, config)» «port.name» «ENDFOR»){
	«IF comp.presentParentComponent»
	super(«FOR port : comp.parent.loadedSymbol.allOutgoingPorts» «port.name» «ENDFOR»);
«ENDIF»
«FOR port : comp.outgoingPorts»
	  this->«port.name» = «port.name»; 
«ENDFOR»
}
«ENDIF»

«FOR port : comp.outgoingPorts»
«Utils.printTemplateArguments(comp)»
tl::optional<«helper.getRealPortCppTypeString(port, config)»> 
«compname»Result«Utils.printFormalTypeParameters(comp, false)»::get«port.name.toFirstUpper»() const
{
  return «port.name»;
}

«Utils.printTemplateArguments(comp)»
void 
«compname»Result«Utils.printFormalTypeParameters(comp, false)»::set«port.name.toFirstUpper»(«helper.getRealPortCppTypeString(port, config)» «port.name»)
{
  this->«port.name» = «port.name»; 
}
«IF ComponentHelper.portUsesCdType(port) »
«var cdeImportStatementOpt = ComponentHelper.getCppImportExtension(port, config)»
«IF cdeImportStatementOpt.isPresent()»
 «var fullImportStatemantName = cdeImportStatementOpt.get.getSymbol.getFullName.split("\\.")»
 «var adapterName = fullImportStatemantName.get(0)+"Adapter"»

tl::optional<«cdeImportStatementOpt.get.getImportClass().toString()»>
«compname»Result«Utils.printFormalTypeParameters(comp, false)»::get«port.name.toFirstUpper»Adap() const
{
  if (!get«port.name.toFirstUpper»().has_value()) {
          return {};
      }

      «adapterName.toFirstUpper» «adapterName.toFirstLower»;
      return «adapterName.toFirstLower».convert(*get«port.name.toFirstUpper»());
}

void
«compname»Result«Utils.printFormalTypeParameters(comp, false)»::set«port.name.toFirstUpper»(«cdeImportStatementOpt.get.getImportClass().toString()» element)
{
  «adapterName.toFirstUpper» «adapterName.toFirstLower»;
      this->«port.name» = «adapterName.toFirstLower».convert(element);
}

«ENDIF»
«ENDIF»
«ENDFOR»
'''
	}
	
	def static generateResultHeader(ComponentTypeSymbol comp, String compname, ConfigParams config){
	    var ComponentHelper helper = new ComponentHelper(comp)
return '''
#pragma once
#include <string>
#include "Port.h"
#include <string>
#include <map>
#include <vector>
#include <list>
#include <set>
«Ports.printIncludes(comp, config)»

«Utils.printNamespaceStart(comp)»

«Utils.printTemplateArguments(comp)»
class «compname»Result
  «IF comp.presentParentComponent» : 
    «Utils.printSuperClassFQ(comp)»Result
    «IF comp.parent.loadedSymbol.hasTypeParameter()»<
    «FOR scTypeParams : helper.superCompActualTypeArguments SEPARATOR ','»
        «scTypeParams»
        «ENDFOR» > «ENDIF»
    «ENDIF»
{
private:
	«FOR port : comp.outgoingPorts»
	tl::optional<«helper.getRealPortCppTypeString(port, config)»> «port.name»;
	«ENDFOR»
public:	
	«compname»Result() = default;
	«IF !comp.allOutgoingPorts.empty»
	«compname»Result(«FOR port : comp.allOutgoingPorts SEPARATOR ','» «helper.getRealPortCppTypeString(port, config)» «port.name» «ENDFOR»);
	«ENDIF»
	«FOR port : comp.outgoingPorts»
    tl::optional<«helper.getRealPortCppTypeString(port, config)»> get«port.name.toFirstUpper»() const;
	void set«port.name.toFirstUpper»(tl::optional<«helper.getRealPortCppTypeString(port, config)»>);
	«IF ComponentHelper.portUsesCdType(port) »
    «var cdeImportStatementOpt = ComponentHelper.getCppImportExtension(port, config)»
    «IF cdeImportStatementOpt.isPresent()»
    tl::optional<«cdeImportStatementOpt.get.getImportClass().toString()»> get«port.name.toFirstUpper»Adap() const;
    void set«port.name.toFirstUpper»(«cdeImportStatementOpt.get.getImportClass().toString()»);
    «ENDIF»
    «ENDIF»
	«ENDFOR»
};

«IF comp.hasTypeParameter()»
  «generateResultBody(comp, compname, config)»
«ENDIF»
«Utils.printNamespaceEnd(comp)»
'''
		
	}
}