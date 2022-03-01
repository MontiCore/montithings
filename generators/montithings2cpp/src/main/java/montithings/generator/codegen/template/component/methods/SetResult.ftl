<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/Preamble.ftl">
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::setResult(${compname}Result${Utils.printFormalTypeParameters(comp)} result){
<#list comp.getOutgoingPorts() as portOut >

  if(result.get${portOut.getName()?cap_first}().has_value()) {
    this->${Identifier.getInterfaceName()}.getPort${portOut.getName()?cap_first}()->setNextValue(
      result.get${portOut.getName()?cap_first}Message(
          ${tc.includeArgs("template.logtracing.hooks.GetCurrOutputUuid", [comp, config, portOut])}
        )
    );
  }
</#list>
}