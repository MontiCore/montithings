<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/impl/helper/GeneralPreamble.ftl">
<#assign isLogTracingEnabled = config.getLogTracing().toString() == "ON">

${Utils.printTemplateArguments(comp)}
${compname}Result${generics} ${className}${generics}::compute(${compname}Input${generics}
${Identifier.getInputName()}){
${compname}Result${generics} ${Identifier.getResultName()};
${compname}State${generics} state__at__pre = ${Identifier.getStateName()};
${ComponentHelper.printStatementBehavior(comp, isLogTracingEnabled)}
<#list ComponentHelper.getPublishedPortsForBehavior(comp) as port>
  ${Identifier.getResultName()}.set${port.getName()?capitalize}(tl::nullopt);
</#list>
return ${Identifier.getResultName()};
}