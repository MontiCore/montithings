${tc.signature("comp","subcomponent")}
if (comp->get${subcomponent.getName()?cap_first}IP().length() == 0
<#list  comp.getAstNode().getConnectors() as connector>
    <#list connector.targetList as target>
    <#-- TODO: What happens when !target.isPresentComponent() -->
        <#if target.isPresentComponent() && subcomponent.getName() == target.getComponent()>
            <#if connector.getSource().isPresentComponent()>
                && comp->get${connector.getSource().getComponent()?cap_first}IP().length() != 0
            </#if>
        </#if>
    </#list>
</#list>
) {