<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::setResult(${compname}Result${Utils.printFormalTypeParameters(comp)} result){
<#list comp.getOutgoingPorts() as portOut >


  this->${Identifier.getInterfaceName()}.getPort${portOut.getName()?cap_first}()->setNextValue(
    result.get${portOut.getName()?cap_first}Message(
        <#if config.getLogTracing().toString() == "ON">logTracer->newOutput()</#if>
      )
  );

  ${tc.includeArgs("template.logtracing.hooks.HandleOutput", [comp, config])}
</#list>
}