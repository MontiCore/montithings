<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::onEvent ()
{
if (timeMode == EVENTBASED)
{
this->compute();
}
<#if needsProtobuf>
  <#list comp.getOutgoingPorts() as p>
    if(${p.getName()}_protobuf->hasValue(this->uuid)){
      result__cache.set${p.getName()?cap_first} (${p.getName()}_protobuf->getCurrentValue(this->uuid)->getPayload());
      checkPostconditions (input__cache, result__cache, state, state__at__pre);
      interface.getPort${p.getName()?cap_first} ()->setNextValue (result__cache.get${p.getName()?cap_first}Message ());
    }
  </#list>
</#if>
}