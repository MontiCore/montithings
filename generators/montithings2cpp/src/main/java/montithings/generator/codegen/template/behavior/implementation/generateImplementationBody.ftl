<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname","className")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Identifier = tc.instantiate("montithings.generator.codegen.util.Identifier")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign generics = Utils.printFormalTypeParameters(comp)>
<#if ComponentHelper.hasBehavior(comp)>
  ${Utils.printTemplateArguments(comp)}
  ${compname}Result${generics} ${className}${generics}::getInitialValues(){
  return {};
  }

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
</#if>

<#list ComponentHelper.getEveryBlocks(comp) as everyBlock>
  ${Utils.printTemplateArguments(comp)}
  ${compname}Result${generics}
  ${className}${generics}::compute${ComponentHelper.getEveryBlockName(comp, everyBlock)}
  (${compname}Input${generics} ${Identifier.getInputName()})
  {
    ${compname}Result${generics} ${Identifier.getResultName()};
    ${ComponentHelper.printJavaBlock(everyBlock.getMCJavaBlock())}
    <#list ComponentHelper.getPublishedPorts(comp, everyBlock.getMCJavaBlock()) as port>
      ${Identifier.getResultName()}.set${port.getName()?capitalize}(tl::nullopt);
    </#list>
    return ${Identifier.getResultName()};
  }
</#list>
