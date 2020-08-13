// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend

import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.helper.ComponentHelper
import arcbasis._symboltable.ComponentTypeSymbol
import montithings.generator.codegen.xtend.util.Ports
import montithings.generator.codegen.ConfigParams

class Input {

  def static generateImplementationFile(ComponentTypeSymbol comp, String compname, ConfigParams config) {
    return '''
    #include "«compname»Input.h"
    «Utils.printNamespaceStart(comp)»
    «IF !comp.hasTypeParameter»
    «generateInputBody(comp, compname, config)»
    «ENDIF»
    «Utils.printNamespaceEnd(comp)»
    '''
  }
  
  
    def static generateInputBody(ComponentTypeSymbol comp, String compname, ConfigParams config) {
    var ComponentHelper helper = new ComponentHelper(comp)
    var isBatch = ComponentHelper.usesBatchMode(comp);
    
    return '''
«IF !isBatch»

«IF !comp.allIncomingPorts.empty»
«Utils.printTemplateArguments(comp)»
«compname»Input«Utils.printFormalTypeParameters(comp, false)»::«compname»Input(«FOR port : comp.allIncomingPorts SEPARATOR ','» tl::optional<«helper.getRealPortCppTypeString(port, config)»> «port.name» «ENDFOR»){
«IF comp.presentParentComponent»
  super(«FOR port : comp.parent.loadedSymbol.allIncomingPorts» «port.name» «ENDFOR»);
«ENDIF»
«FOR port : comp.incomingPorts»
  this->«port.name» = std::move(«port.name»); 
«ENDFOR»
}
«ENDIF»
«ENDIF»  
«FOR port : ComponentHelper.getPortsInBatchStatement(comp)»
«IF port.isIncoming»
«Utils.printTemplateArguments(comp)»
std::vector<«helper.getRealPortCppTypeString(port, config)»> 
«compname»Input«Utils.printFormalTypeParameters(comp, false)»::get«port.name.toFirstUpper»() const
{
  return «port.name»;
}

«Utils.printTemplateArguments(comp)»
void 
«compname»Input«Utils.printFormalTypeParameters(comp, false)»::add«port.name.toFirstUpper»Element(tl::optional<«helper.getRealPortCppTypeString(port, config)»> element)
{
  if (element)
    {
    «port.name».push_back(element.value());
     }
}

«Utils.printTemplateArguments(comp)»
void 
«compname»Input«Utils.printFormalTypeParameters(comp, false)»::set«port.name.toFirstUpper»(std::vector<«helper.getRealPortCppTypeString(port, config)»> vector)
{
  this->«port.name» = std::move(vector);
}
«ENDIF»
«ENDFOR»

«FOR port : ComponentHelper.getPortsNotInBatchStatements(comp)»
«IF port.isIncoming»
«Utils.printTemplateArguments(comp)»
tl::optional<«helper.getRealPortCppTypeString(port, config)»> 
«compname»Input«Utils.printFormalTypeParameters(comp, false)»::get«port.name.toFirstUpper»() const
{
  return «port.name»;
}

«Utils.printTemplateArguments(comp)»
void 
«compname»Input«Utils.printFormalTypeParameters(comp, false)»::set«port.name.toFirstUpper»(tl::optional<«helper.getRealPortCppTypeString(port, config)»> element)
{
  this->«port.name» = std::move(element);
} 
«ENDIF»
«ENDFOR»

'''
  }
  
  def static generateInputHeader(ComponentTypeSymbol comp, String compname, ConfigParams config) {
  var ComponentHelper helper = new ComponentHelper(comp)
  var isBatch = ComponentHelper.usesBatchMode(comp);
    
    return '''
#pragma once
#include <string>
#include "Port.h"
#include <string>
#include <map>
#include <vector>
#include <list>
#include <set>
#include <utility>
#include "tl/optional.hpp"
«Ports.printIncludes(comp, config)»

«Utils.printNamespaceStart(comp)»

«Utils.printTemplateArguments(comp)»
class «compname»Input
«IF comp.presentParentComponent» : 
  «Utils.printSuperClassFQ(comp)»Input
«IF comp.parent.loadedSymbol.hasTypeParameter»<
«FOR scTypeParams : helper.superCompActualTypeArguments SEPARATOR ','»
    «scTypeParams»
«ENDFOR»>«ENDIF»
«ENDIF»
{
private:
«FOR port : ComponentHelper.getPortsNotInBatchStatements(comp)»
  tl::optional<«helper.getRealPortCppTypeString(port, config)»> «port.name»;
«ENDFOR»
«FOR port : ComponentHelper.getPortsInBatchStatement(comp)»
  std::vector<«helper.getRealPortCppTypeString(port, config)»> «port.name» = {};
«ENDFOR»
public:
  «compname»Input() = default;
  «IF !comp.allIncomingPorts.empty && !isBatch»
  explicit «compname»Input(«FOR port : comp.allIncomingPorts SEPARATOR ','» tl::optional<«helper.getRealPortCppTypeString(port, config)»> «port.name» «ENDFOR»);
    «ENDIF»
  «FOR port : ComponentHelper.getPortsNotInBatchStatements(comp)»
  «IF port.isIncoming»
  tl::optional<«helper.getRealPortCppTypeString(port, config)»> get«port.name.toFirstUpper»() const;
  void set«port.name.toFirstUpper»(tl::optional<«helper.getRealPortCppTypeString(port, config)»>);
  «ENDIF»
  «ENDFOR»
  «FOR port : ComponentHelper.getPortsInBatchStatement(comp)»
  «IF port.isIncoming»
  std::vector<«helper.getRealPortCppTypeString(port, config)»> get«port.name.toFirstUpper»() const;
  void add«port.name.toFirstUpper»Element(tl::optional<«helper.getRealPortCppTypeString(port, config)»>);
  void set«port.name.toFirstUpper»(std::vector<«helper.getRealPortCppTypeString(port, config)»> vector);
  «ENDIF»
  «ENDFOR»
};

«IF comp.hasTypeParameter»
  «generateInputBody(comp, compname, config)»
«ENDIF»
«Utils.printNamespaceEnd(comp)»
'''
  }
  
}