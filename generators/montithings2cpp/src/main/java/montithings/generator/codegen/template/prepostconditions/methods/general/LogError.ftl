<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "isPrecondition", "existsHWC")}
<#include "/template/prepostconditions/helper/GeneralPreamble.ftl">


${Utils.printTemplateArguments(comp)}
void
${className}${generics}::logError (${compname}State${generics} ${Identifier.getStateName()},
${compname}Input${generics} ${Identifier.getInputName()}
<#if !isPrecondition>
  , ${compname}Result${generics} ${Identifier.getResultName()}
  , ${compname}State${generics} ${Identifier.getStateName()}__at__pre
</#if>) const
{
std::stringstream error;
error << "Violated <#if !isPrecondition>pre<#else>post</#if>condition " << toString () << " on component '" << instanceName << "'" << std::endl;
error << "Input port values: " << std::endl;
<#list ComponentHelper.getPortsNotInBatchStatements(comp) as inPort>
    <#if inPort.isIncoming()>
      if (${Identifier.getInputName()}.get${inPort.getName()?cap_first} ().has_value()) {
      error << "Port \"${inPort.getName()}\": " << ${Identifier.getInputName()}.get${inPort.getName()?cap_first} ().value() << std::endl;
      } else {
      error << "Port \"${inPort.getName()}\": No data." << std::endl;
      }
    </#if>
</#list>
<#list ComponentHelper.getPortsInBatchStatement(comp) as inPort>
  if (${Identifier.getInputName()}.get${inPort.getName()?cap_first} ().has_value()) {
  error << "Port \"${inPort.getName()}\": " << ${Identifier.getInputName()}.get${inPort.getName()?cap_first} () << std::endl;
  } else {
  error << "Port \"${inPort.getName()}\": No data." << std::endl;
  }
</#list>
<#if !isPrecondition>
    <#list comp.getAllOutgoingPorts() as outPort>
      if (${Identifier.getResultName()}.get${outPort.getName()?cap_first} ().has_value()) {
      error << "Out port \"${outPort.getName()}\": " << ${Identifier.getResultName()}.get${outPort.getName()?cap_first} ().value() << std::endl;
      } else {
      error << "Out port \"${outPort.getName()}\": No data." << std::endl;
      }
    </#list>
</#if>
LOG (FATAL) << error.str ();
}