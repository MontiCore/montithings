<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::initialize(){

${tc.includeArgs("template.component.helper.DDSRestoreRecordedState", [comp, config])}
${tc.includeArgs("template.component.helper.DDSInjectRecordedData", [comp, config])}


<#list comp.incomingPorts as port >
  ${Identifier.getInterfaceName()}.getPort${port.getName()?cap_first} ()->attach (this);
  <#assign additionalPort = GeneratorHelper.getPortHwcTemplateName(port, config)>
  <#if config.getTemplatedPorts()?seq_contains(port) && additionalPort!="Optional.empty">
    <#assign type = ComponentHelper.getRealPortCppTypeString(port.getComponent().get(), port, config)>
    ${Identifier.getInterfaceName()}.addInPort${port.getName()?cap_first}(new ${Names.getSimpleName(additionalPort.get())?cap_first}<${type}>(instanceName
    <#if config.getMessageBroker().toString() == "DDS">
        , argc, &argv
    </#if>));
  </#if>
</#list>
<#list comp.outgoingPorts as port >
  <#assign additionalPort = GeneratorHelper.getPortHwcTemplateName(port, config)>
  <#if config.getTemplatedPorts()?seq_contains(port) && additionalPort!="Optional.empty">
    <#assign type = ComponentHelper.getRealPortCppTypeString(port.getComponent().get(), port, config)>
    ${Identifier.getInterfaceName()}.addOutPort${port.getName()?cap_first}(new ${Names.getSimpleName(additionalPort.get())?cap_first}<${type}>(instanceName
    <#if config.getMessageBroker().toString() == "DDS">
            , argc, &argv
    </#if>));
  </#if>
</#list>
<#if ComponentHelper.retainState(comp)>
  if (!${Identifier.getStateName()}.isRestoredState ())
</#if>
{
${compname}Result${Utils.printFormalTypeParameters(comp)} result = ${Identifier.getBehaviorImplName()}.getInitialValues();
setResult(result);
}
}