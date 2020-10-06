${tc.signature("comp","compname")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
${Utils.printTemplateArguments(comp)}
void
${compname}${Utils.printFormalTypeParameters(comp)}::run ()
{
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