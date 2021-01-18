<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname","config","className")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign GeneratorHelper = tc.instantiate("montithings.generator.helper.GeneratorHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign Identifier = tc.instantiate("montithings.generator.codegen.util.Identifier")>
<#assign Names = tc.instantiate("de.se_rwth.commons.Names")>

${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::initialize(){
<#list comp.incomingPorts as port >
  getPort${port.getName()?cap_first} ()->attach (this);
  <#assign additionalPort = GeneratorHelper.getPortHwcTemplateName(port, config)>
  <#if config.getTemplatedPorts()?seq_contains(port) && additionalPort!="Optional.empty">
    <#assign type = ComponentHelper.getRealPortCppTypeString(port.getComponent().get(), port, config)>
    addInPort${port.getName()?cap_first}(new ${Names.getSimpleName(additionalPort.get())?cap_first}<${type}>());
  </#if>
</#list>
<#list comp.outgoingPorts as port >
  <#assign additionalPort = GeneratorHelper.getPortHwcTemplateName(port, config)>
  <#if config.getTemplatedPorts()?seq_contains(port) && additionalPort!="Optional.empty">
    <#assign type = ComponentHelper.getRealPortCppTypeString(port.getComponent().get(), port, config)>
    addOutPort${port.getName()?cap_first}(new ${Names.getSimpleName(additionalPort.get())?cap_first}<${type}>());
  </#if>
</#list>
<#if ComponentHelper.retainState(comp)>
  if (!${Identifier.getBehaviorImplName()}.isRestoredState ())
</#if>
{
${compname}Result${Utils.printFormalTypeParameters(comp)} result = ${Identifier.getBehaviorImplName()}.getInitialValues();
setResult(result);
}
}