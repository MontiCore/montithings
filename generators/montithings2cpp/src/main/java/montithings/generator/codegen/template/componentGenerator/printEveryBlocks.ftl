<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname","className")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#list ComponentHelper.getEveryBlocks(comp) as everyBlock>
<#assign Identifier = tc.instantiate("montithings.generator.codegen.util.Identifier")>

<#assign everyname = ComponentHelper.getEveryBlockName(comp, everyBlock)>
${Utils.printTemplateArguments(comp)}
void
${className}${Utils.printFormalTypeParameters(comp)}::run${everyname} ()
{

LOG(DEBUG) << "Every-thread ${everyname?replace("__", "")} for ${compname} started";
while (true)
{
auto end = std::chrono::high_resolution_clock::now()
+ ${ComponentHelper.getExecutionIntervalMethod(comp, everyBlock)};
this->compute${everyname}();

if (std::chrono::high_resolution_clock::now() > end)
{
LOG(WARNING) << "Execution of EveryBlock ${everyname?replace("__", "")} took longer than its interval";
}

do {
std::this_thread::yield();
std::this_thread::sleep_for(std::chrono::milliseconds(1));
} while (std::chrono::high_resolution_clock::now()  < end);
}
}
</#list>