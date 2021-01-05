<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

std::cout << "Started." << std::endl;

while (true)
{
auto end = std::chrono::high_resolution_clock::now()
+ ${ComponentHelper.getExecutionIntervalMethod(comp)};
<#if ComponentHelper.isTimesync(comp)>
  cmp.compute();
</#if>
do {
std::this_thread::yield();
<#if ComponentHelper.isTimesync(comp)>
  std::this_thread::sleep_for(std::chrono::milliseconds(1));
<#else>
  std::this_thread::sleep_for(std::chrono::milliseconds(1000));
</#if>
} while (std::chrono::high_resolution_clock::now() < end);
}