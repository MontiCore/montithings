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
  return ${Identifier.getResultName()};
  }
</#if>

${Utils.printTemplateArguments(comp)}
void ${className}${generics}::setInstanceName (const std::string &instanceName)
{
this->instanceName = instanceName;
}

<#list ComponentHelper.getEveryBlocks(comp) as everyBlock>
${Utils.printTemplateArguments(comp)}
void
${className}${generics}::compute_Every${ComponentHelper.getEveryBlockName(comp, everyBlock)} ()
{
${ComponentHelper.printJavaBlock(everyBlock.getMCJavaBlock())}
}
</#list>

<#list ComponentHelper.getVariablesAndParameters(comp) as var>
  <#assign type = ComponentHelper.printCPPTypeName(var.getType(), comp, config)>
  <#assign varName = var.getName()>

  ${Utils.printTemplateArguments(comp)}
  ${type} ${className}${generics}::get${varName?cap_first}() const
  {
    return ${varName};
  }

  ${Utils.printTemplateArguments(comp)}
  void ${className}${generics}::set${varName?cap_first}(${type} ${varName})
  {
    ${className}${generics}::${varName} = ${varName};
  }
</#list>

${tc.includeArgs("template.behavior.implementation.printStateMethods", [comp, className])}