<#-- (c) https://github.com/MontiCore/monticore -->
<#--package montithings.generator.codegen.xtend.behavior

import arcbasis._symboltable.ComponentTypeSymbol
import montithings.generator.codegen.xtend.util.Utils-->



  <#--

  Implementing this method is mandatory.

  @return the implementation of the compute() method
 -->
<#--def String printCompute(ComponentTypeSymbol comp, String compname);-->

<#--

Implementing this method is mandatory.

@return the implementation of the getInitialValues() method
-->
<#--def String printGetInitialValues(ComponentTypeSymbol comp, String compname);-->

  <#--

  This method can be used to add additional code to the implementation class without.
 -->
<#--def String hook(ComponentTypeSymbol comp, String compname);-->

<#--

Entry point for generating a component's implementation.

-->
<#macro generateHeader comp compname>
    <#assign generics = Utils.printFormalTypeParameters(comp, false)>
#pragma once
#include "${compname}Input.h"
#include "${compname}Result.h"
#include "IComputable.h"
#include <stdexcept>
${Utils.printNamespaceStart(comp)}

${Utils.printTemplateArguments(comp)}
class ${compname}Impl : public IComputable<${compname}Input${generics},${compname}Result${generics}>{ {
private:
    ${Utils.printVariables(comp)}
    ${Utils.printConfigParameters(comp)}


public:
    ${hook(comp, compname)}
  ${printConstructor(comp, compname)}
  virtual ${compname}Result${generics} getInitialValues() override;
  virtual ${compname}Result${generics} compute(${compname}Input${generics} input) override;

    };

    <#if comp.hasTypeParameter()>
 ${generateBody(comp, compname)}
 </#if>
${Utils.printNamespaceEnd(comp)}
  </#macro>

  <#macro generateImplementationFile comp compname >
  #include "${compname}Impl.h"
  ${Utils.printNamespaceStart(comp)}
  <#if !comp.hasTypeParameter()>
 ${generateBody(comp, compname)}
 </#if>
  ${Utils.printNamespaceEnd(comp)}
  </#macro>

  <#macro generateBody comp compname>
${printGetInitialValues(comp, compname)}

${printCompute(comp, compname)}
  </#macro>



  <#macro printConstructor comp compname>
${compname}Impl(${Utils.printConfigurationParametersAsList(comp)})
<#if !comp.getParameters().isEmpty>
 :
 </#if>
<#list comp.getParameters() as param >
 ${param.getName()} (${param.getName()})<#sep>,
 </#list>
{
}
  </#macro>
