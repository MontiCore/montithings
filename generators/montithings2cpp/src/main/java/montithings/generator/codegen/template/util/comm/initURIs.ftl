<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp")}
<#list comp.ports as p >
    std::string ${p.getName()}_uri;
</#list>
<#list comp.subComponents as subcomponent >
    std::string ${subcomponent.getName()}_uri;
</#list>
<#list comp.getAstNode().getConnectors() as connector>
    <#list connector.targetList as target>
        <#list comp.subComponents as subcomponent>
            <#assign subcomponentSymbol = subcomponent.type.loadedSymbol>
            <#if !connector.getSource().isPresentComponent() && subcomponent.getName() == target.getComponent()>
                <#list subcomponentSymbol.ports as p>
                    <#if p.getName() == target.port>
                        std::string to${subcomponent.getName()?cap_first}_${p.getName()}_uri;
                    </#if>
                </#list>
            </#if>
        </#list>
    </#list>
</#list>
std::string comm_in_uri;
std::string comm_out_uri;