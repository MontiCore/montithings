<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/impl/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
${compname}Result${generics} ${className}${generics}::compute(${compname}Input${generics}
${Identifier.getInputName()}){
${compname}Result${generics} ${Identifier.getResultName()};
${ComponentHelper.printStatementBehavior(comp)}
<#list ComponentHelper.getPublishedPortsForBehavior(comp) as port>
  ${Identifier.getResultName()}.set${port.getName()?capitalize}(tl::nullopt);
</#list>
return ${Identifier.getResultName()};
}