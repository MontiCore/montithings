# (c) https://github.com/MontiCore/monticore
${tc.signature("comp", "compname", "config")}

<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#--package montithings.generator.codegen.xtend

import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.helper.ComponentHelper
import arcbasis._symboltable.ComponentTypeSymbol
import montithings.generator.codegen.xtend.util.Ports
import montithings.generator.codegen.ConfigParams-->

    #include "${compname}Result.h"
    ${Utils.printNamespaceStart(comp)}
    <#if !comp.hasTypeParameter()>
 <@generateResultBody comp compname config/>
 </#if>
    ${Utils.printNamespaceEnd(comp)}



<#macro  generateResultBody comp, compname, config>
<#if !comp.getAllOutgoingPorts()?has_content>
${Utils.printTemplateArguments(comp)}
${compname}Result${Utils.printFormalTypeParameters(comp, false)}::${compname}Result(<#list comp.getAllOutgoingPorts() as port> ${ComponentHelper.getRealPortCppTypeString(comp, port, config)}
 ${port.getName()} <#sep>,
 </#list>){
  <#if comp.isPresentParentComponent()>
  super(<#list comp.parent().loadedSymbol.getAllOutgoingPorts() as port >
 port.getName()
 </#list>);
</#if>
<#list comp.getOutgoingPorts() as port >
 this->${port.getName()} = ${port.getName()};
 </#list>
}
</#if>

<#list comp.getOutgoingPorts() as port>
${Utils.printTemplateArguments(comp)}
tl::optional<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}>
${compname}Result${Utils.printFormalTypeParameters(comp, false)}::get${port.getName()?cap_first}() const
{
  return ${port.getName()};
}

${Utils.printTemplateArguments(comp)}
void 
${compname}Result${Utils.printFormalTypeParameters(comp, false)}::set${port.getName()?cap_first}(tl::optional<${ComponentHelper.getRealPortCppTypeString(comp, port, config)}> ${port.getName()})
{
  this->${port.getName()} = ${port.getName()};
}
<#if ComponentHelper.portUsesCdType(port)>
<#assign cdeImportStatementOpt = ComponentHelper.getCppImportExtension(port, config)>
<#if cdeImportStatementOpt.isPresent()>
 <#assign fullImportStatemantName = cdeImportStatementOpt.get.getSymbol.getFullName.split("\\.")>
 <#assign adapterName = fullImportStatemantName.get(0)+"Adapter">

tl::optional<${cdeImportStatementOpt.get.getImportClass().toString()}>
${compname}Result${Utils.printFormalTypeParameters(comp, false)}::get${port.getName()?cap_first}Adap() const
{
  if (!get${port.getName()?cap_first}().has_value()) {
          return {};
      }

      ${adapterName?cap_first} ${adapterName?uncap_first};
      return ${adapterName?uncap_first}.convert(*get${port.getName()?cap_first}());
}

void
${compname}Result${Utils.printFormalTypeParameters(comp, false)}::set${port.getName()?cap_first}(${cdeImportStatementOpt.get.getImportClass().toString()} element)
{
  ${adapterName?cap_first} ${adapterName?uncap_first};
      this->${port.getName()} = ${adapterName?uncap_first}.convert(element);
}

</#if>
</#if>
</#list>
</#macro>