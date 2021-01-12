<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname","className")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
${Utils.printTemplateArguments(comp)}
void
${className}${Utils.printFormalTypeParameters(comp)}::run ()
{
bool hasUpdateInterval = ${ComponentHelper.hasUpdateInterval(comp)?c};

if (timeMode == TIMESYNC || hasUpdateInterval) {
std::cout << "Thread for ${compname} started\n";
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