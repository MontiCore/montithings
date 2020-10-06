<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>

    <#if config.getSplittingMode().toString() == "OFF">
        <#list comp.getSubComponents() as subcomponent>
            <#assign type = ComponentHelper.getSubComponentTypeNameWithoutPackage(subcomponent, config)>
            ${Utils.printPackageNamespace(comp, subcomponent)}${type} ${subcomponent.getName()};
        </#list>
    <#else>
        <#list comp.getSubComponents() as subcomponent >
          std::string subcomp${subcomponent.getName()?cap_first}IP;
        </#list>
    </#if>


