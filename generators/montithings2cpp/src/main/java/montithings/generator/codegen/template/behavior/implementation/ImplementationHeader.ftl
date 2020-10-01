# (c) https://github.com/MontiCore/monticore
<#--package montithings.generator.codegen.xtend.behavior

import arcbasis._symboltable.ComponentTypeSymbol
import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.codegen.xtend.util.Identifier
import montithings.generator.helper.ComponentHelper-->

  def static generateImplementationHeader(ComponentTypeSymbol comp, String compname, boolean existsHWC) {
    <#assign String generics = Utils.printFormalTypeParameters(comp);>
    return '''
#pragma once
#include "${compname}Input.h"
#include "${compname}Result.h"
#include "IComputable.h"
#include <stdexcept>
${Utils.printNamespaceStart(comp)}

${Utils.printTemplateArguments(comp)}
class ${compname}Impl<#if existsHWC>
 TOP
 </#if> : public IComputable<${compname}Input${generics},${compname}Result${generics}>{

protected:  
    ${Utils.printVariables(comp)}
    <#-- Currently useless. MontiArc 6's getFields() returns both variables and parameters -->
    <#-- ${Utils.printConfigParameters(comp)} -->
public:
  ${printConstructor(comp, existsHWC)}

  <#if ComponentHelper.hasBehavior(comp)>
  ${compname}Result${generics} getInitialValues() override;
  ${compname}Result${generics} compute(${compname}Input${generics} input) override;
  ${ELSE}
  ${compname}Result${generics} getInitialValues() = 0;
  ${compname}Result${generics} compute(${compname}Input${generics} input) = 0;
  </#if>
};

<#if comp.hasTypeParameter>
 ${generateImplementationBody(comp, compname, existsHWC)}
 </#if>
${Utils.printNamespaceEnd(comp)}
'''
  }
