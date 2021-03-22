<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname","config","className")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign GeneratorHelper = tc.instantiate("montithings.generator.helper.GeneratorHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign Identifier = tc.instantiate("montithings.generator.codegen.util.Identifier")>
<#assign Names = tc.instantiate("de.se_rwth.commons.Names")>

${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::setResult(${compname}Result${Utils.printFormalTypeParameters(comp)} result){
<#list comp.getOutgoingPorts() as portOut >
  this->${Identifier.getInterfaceName()}.getPort${portOut.getName()?cap_first}()->setNextValue(result.get${portOut.getName()?cap_first}());
</#list>
}