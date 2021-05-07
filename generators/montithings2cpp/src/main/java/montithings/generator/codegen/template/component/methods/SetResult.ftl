<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::setResult(${compname}Result${Utils.printFormalTypeParameters(comp)} result){
<#list comp.getOutgoingPorts() as portOut >

  <#-- ${tc.includeArgs("template.logtracing.hooks.PrepareResult", [comp, config, portOut])}-->

  this->${Identifier.getInterfaceName()}.getPort${portOut.getName()?cap_first}()->setNextValue(
   <#--  <#if config.getLogTracing().toString() == "ON">
      ${portOut.getName()}Wrapped
    <#else>-->
      result.get${portOut.getName()?cap_first}()
    <#-- </#if>-->
  );
</#list>
}