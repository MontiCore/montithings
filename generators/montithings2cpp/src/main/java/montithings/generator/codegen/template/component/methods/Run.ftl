<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/Preamble.ftl">
<#include "/template/component/helper/GeneralPreamble.ftl">


${Utils.printTemplateArguments(comp)}
void
${className}${Utils.printFormalTypeParameters(comp)}::run ()
{
bool hasUpdateInterval = ${ComponentHelper.hasUpdateInterval(comp)?c};

if (timeMode == TIMESYNC || hasUpdateInterval) {
LOG(DEBUG) << "Thread for ${compname} started";
while (true)
{
auto end = std::chrono::high_resolution_clock::now()
+ ${ComponentHelper.getExecutionIntervalMethod(comp)};
this->compute();

do {
std::this_thread::yield();
std::this_thread::sleep_for(std::chrono::milliseconds(1));
} while (std::chrono::high_resolution_clock::now()  < end);
}
}
}