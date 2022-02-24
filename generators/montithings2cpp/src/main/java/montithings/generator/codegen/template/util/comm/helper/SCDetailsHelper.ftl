<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","subcomponent")}
<#include "/template/util/comm/helper/GeneralPreamble.ftl">


if (comp->get${subcomponent.getName()?cap_first}IP().length() == 0
<#list  comp.getAstNode().getConnectors() as connector>
    <#list connector.targetList as target>
    <#-- TODO: What happens when !target.isPresentComponent() -->
        <#if target.isPresentComponent() && subcomponent.getName() == target.getComponent()> <#-- todo long expression-->
            <#if connector.getSource().isPresentComponent()>
                && comp->get${connector.getSource().getComponent()?cap_first}IP().length() != 0
            </#if>
        </#if>
    </#list>
</#list>
) {