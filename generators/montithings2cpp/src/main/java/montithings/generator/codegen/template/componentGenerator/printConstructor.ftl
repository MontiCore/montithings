${tc.signature("comp","compname","config")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign shouldPrintSubcomponents = comp.subComponents?has_content && (config.getSplittingMode().toString() == "OFF")>
${Utils.printTemplateArguments(comp)}
${compname}${Utils.printFormalTypeParameters(comp)}::${compname}(std::string instanceName<#if comp.getParameters()?has_content>
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
<#if comp.isPresentParentComponent()>
    super(<#list getInheritedParams(comp) as inhParam >
    inhParam<#sep>,</#sep>
</#list>);
</#if>
}