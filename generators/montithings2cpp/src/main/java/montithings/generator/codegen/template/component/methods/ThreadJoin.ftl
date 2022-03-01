<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/Preamble.ftl">
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::threadJoin ()
{
for (int i = 0; i < threads.size (); i++)
{
threads[i].join ();
}
<#if splittingModeDisabled>
  <#list comp.subComponents as subcomponent >
    this->${subcomponent.getName()}.threadJoin();
  </#list>
</#if>
}