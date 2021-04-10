<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "className")}
<#include "/template/state/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
json ${className}${generics}::serializeState ()
{
json state;
<#list ComponentHelper.getFields(comp) as variable>
  state["${variable.getName()}"] = dataToJson (${variable.getName()});
</#list>
return state;
}