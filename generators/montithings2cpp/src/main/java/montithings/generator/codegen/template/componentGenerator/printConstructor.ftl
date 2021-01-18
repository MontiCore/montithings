<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname","config","className")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign Identifier = tc.instantiate("montithings.generator.codegen.util.Identifier")>
<#assign shouldPrintSubcomponents = comp.subComponents?has_content && (config.getSplittingMode().toString() == "OFF")>
${Utils.printTemplateArguments(comp)}
${className}${Utils.printFormalTypeParameters(comp)}::${className}(std::string instanceName<#if comp.getParameters()?has_content>
  ,
</#if>${Utils.printConfigurationParametersAsList(comp)})
<#if comp.isAtomic() || comp.getParameters()?has_content || shouldPrintSubcomponents>
  :
</#if>
<#if comp.isAtomic()>
    ${tc.includeArgs("template.componentGenerator.printBehaviorInitializerListEntry", [comp, compname])}
</#if>
<#if comp.isAtomic() && comp.getParameters()?has_content>
  ,
</#if>
<#if shouldPrintSubcomponents>
  ${tc.includeArgs("template.util.subcomponents.printInitializerList", [comp, config])}
</#if>
<#if comp.getParameters()?has_content && shouldPrintSubcomponents>,</#if>
<#if comp.isAtomic() && !comp.getParameters()?has_content && shouldPrintSubcomponents>,
</#if>
<#list comp.getParameters() as param >
  ${param.getName()} (${param.getName()})<#sep>,</#sep>
</#list>
{
this->instanceName = instanceName;
<#if comp.isAtomic()>
  this->${Identifier.getBehaviorImplName()}.setInstanceName (instanceName);
  this->${Identifier.getBehaviorImplName()}.setup ();
</#if>
<#if comp.isPresentParentComponent()>
  super(<#list getInheritedParams(comp) as inhParam >
  inhParam<#sep>,</#sep>
</#list>);
</#if>
}