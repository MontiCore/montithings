<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname","className")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#list ComponentHelper.getEveryBlocks(comp) as everyBlock>
<#assign Identifier = tc.instantiate("montithings.generator.codegen.util.Identifier")>

<#assign everyname = everyBlock.getName()>
${Utils.printTemplateArguments(comp)}
void
${className}${Utils.printFormalTypeParameters(comp)}::run_Every${everyname} ()
{

LOG(DEBUG) << "Every-thread ${everyname} for ${compname} started";
while (true)
{
auto end = std::chrono::high_resolution_clock::now()
+ ${ComponentHelper.getExecutionIntervalMethod(comp, everyBlock)};
${Identifier.getBehaviorImplName()}.compute_Every${everyBlock.getName()}();

do {
std::this_thread::yield();
std::this_thread::sleep_for(std::chrono::milliseconds(1));
} while (std::chrono::high_resolution_clock::now()  < end);
}
}
</#list>