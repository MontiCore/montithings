<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>

${Utils.printTemplateArguments(comp)}
bool
${className}${Utils.printFormalTypeParameters(comp)}::restoreState ()
{
// 1. Option: Try to restore state locally from file
if (behaviorImpl.restoreState ())
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
  behaviorImpl.requestState ();
  auto requestStateTimeout = std::chrono::high_resolution_clock::now () + std::chrono::seconds (1);
  while (!behaviorImpl.isReceivedState ()
  && std::chrono::high_resolution_clock::now () < requestStateTimeout)
  ;
  if (behaviorImpl.isReceivedState ())
  {
  behaviorImpl.requestReplay ();
  while (!behaviorImpl.isReplayFinished ())
  ;
  }

  bool restoreSuccessful = behaviorImpl.isReceivedState () && behaviorImpl.isReplayFinished ();
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