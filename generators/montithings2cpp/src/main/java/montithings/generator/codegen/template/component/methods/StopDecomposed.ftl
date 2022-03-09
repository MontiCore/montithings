<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::stop()
{
this->stopSignalReceived = true;
<#if !needsRunMethod>
    <#if splittingModeDisabled || ComponentHelper.shouldIncludeSubcomponents(comp, config)>
        <#list comp.subComponents as subcomponent >
          this->${subcomponent.getName()}.stop();
        </#list>
    </#if>
</#if>
for (int i = 0; i < threads.size (); i++)
{
threads[i].join ();
}
}