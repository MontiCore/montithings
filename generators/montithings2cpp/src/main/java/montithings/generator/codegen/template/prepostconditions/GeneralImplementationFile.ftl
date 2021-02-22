<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "isPrecondition", "existsHWC")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Identifier = tc.instantiate("montithings.generator.codegen.util.Identifier")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign compname = comp.getName()>
<#assign prefix = "">
<#if isPrecondition>
    <#assign prefix = "Pre">
<#else>
    <#assign prefix = "Post">
</#if>
<#assign className = compname + prefix + "condition">
<#if existsHWC>
  <#assign className += "TOP">
</#if>

#include "${compname}${prefix}condition.h"

${Utils.printNamespaceStart(comp)}

${className}::${className} (const std::string &instanceName)
: instanceName (instanceName)
{
}

bool
${className}::isCatched ()
{
return false;
}


void
${className}::apply (${compname}State &state, ${compname}Input &input <#if !isPrecondition>, ${compname}Result &result</#if>)
{
if (check (state, input<#if !isPrecondition>, result</#if>))
{
if (isCatched ())
{
resolve (state, input<#if !isPrecondition>, result</#if>);
}
else
{
logError (state, input<#if !isPrecondition>, result</#if>);
}
}
}


void
${className}::logError (${compname}State state, ${compname}Input ${Identifier.getInputName()}
<#if !isPrecondition>, ${compname}Result ${Identifier.getResultName()}</#if>) const
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

${Utils.printNamespaceEnd(comp)}
