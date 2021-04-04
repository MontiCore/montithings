<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname","config","className")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp, false)}::init(){
<#if comp.isPresentParentComponent()>
    super.init();
</#if>

<#if config.getSplittingMode().toString() == "OFF" || ComponentHelper.shouldIncludeSubcomponents(comp,config)>
    <#list comp.getAstNode().getConnectors() as connector>
        <#list connector.getTargetList() as target>
            <#if ComponentHelper.isIncomingPort(comp, target)>
                // implements "${connector.getSource().getQName()} -> ${target.getQName()}"
                ${Utils.printGetPort(target)}->setDataProvidingPort (${Utils.printGetPort(connector.getSource())});
                <#if ComponentHelper.isSIUnitPort(connector.getSource())>
                    ${Utils.printComponentPrefix(target)}setPort${connector.getSource().getPort()?cap_first}ConversionFactor (${ComponentHelper.getConversionFactorFromSourceAndTarget(connector.source, target)?replace(",", ".")});
                </#if>
            </#if>
        </#list>
    </#list>

    <#list comp.getSubComponents() as subcomponent >
        ${subcomponent.getName()}.init();
    </#list>
</#if>
}