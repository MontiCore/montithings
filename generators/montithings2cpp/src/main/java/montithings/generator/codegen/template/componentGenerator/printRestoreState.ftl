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
std::cout << "Could not restore state from local file." << std::endl;
}

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
std::cout << "Could not restore state from external service." << std::endl;
}

return restoreSuccessful;
}