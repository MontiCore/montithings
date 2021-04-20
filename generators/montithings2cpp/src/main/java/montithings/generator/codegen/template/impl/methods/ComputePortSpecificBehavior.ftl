<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("behavior", "comp", "config", "existsHWC")}
<#include "/template/impl/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
${compname}Result${generics}
${className}${generics}::compute${ComponentHelper.getPortSpecificBehaviorName(comp, behavior)}
(${compname}Input${generics} ${Identifier.getInputName()})
{
${compname}Result${generics} ${Identifier.getResultName()};
${compname}State${generics} state__at__pre = ${Identifier.getStateName()};
${ComponentHelper.printJavaBlock(behavior.getMCJavaBlock())}
<#list ComponentHelper.getPublishedPorts(comp, behavior.getMCJavaBlock()) as port>
    ${Identifier.getResultName()}.set${port.getName()?capitalize}(tl::nullopt);
</#list>
return ${Identifier.getResultName()};
}