<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign Identifier = tc.instantiate("montithings.generator.codegen.util.Identifier")>

${Utils.printTemplateArguments(comp)}
bool
${className}${Utils.printFormalTypeParameters(comp)}::restoreState ()
{
// 1. Option: Try to restore state locally from file
if (${Identifier.getStateName()}.restoreState ())
{
return true;
}
else
{
LOG(DEBUG) << "Component instance '"
           << instanceName
           << "' could not restore state from local file.";
}

<#if config.getMessageBroker().toString() == "MQTT">
  // 2. Option restore state and replay messages since state
  ${Identifier.getStateName()}.requestState ();
  auto requestStateTimeout = std::chrono::high_resolution_clock::now () + std::chrono::seconds (1);
  while (!${Identifier.getStateName()}.isReceivedState ()
  && std::chrono::high_resolution_clock::now () < requestStateTimeout)
  ;
  if (${Identifier.getStateName()}.isReceivedState ())
  {
  ${Identifier.getStateName()}.requestReplay ();
  while (!${Identifier.getStateName()}.isReplayFinished ())
  ;
  }

  bool restoreSuccessful = ${Identifier.getStateName()}.isReceivedState () && ${Identifier.getStateName()}.isReplayFinished ();
  if (!restoreSuccessful)
  {
  LOG(DEBUG) << "Component instance '"
             << instanceName
             << "' could not restore state from external service."
             << std::endl;
  }

  return restoreSuccessful;
<#else>
  return false;
</#if>
}