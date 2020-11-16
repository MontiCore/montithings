<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname","config")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
${Utils.printTemplateArguments(comp)}
void ${compname}${Utils.printFormalTypeParameters(comp, false)}::setUp(TimeMode enclosingComponentTiming){
if (enclosingComponentTiming == TIMESYNC) {timeMode = TIMESYNC;}
<#if comp.isPresentParentComponent()>
    super.setUp(enclosingComponentTiming);
</#if>

<#if config.getMessageBroker().toString() == "MQTT">
${tc.includeArgs("template.util.ports.printAddMqttPorts", [comp, config])}
</#if>

initialize();
}