<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#include "/template/Preamble.ftl">
<#include "/template/component/helper/GeneralPreamble.ftl">


std::vector< std::thread > threads;

std::mutex computeMutex;

// number of compute() that still need to be executed
// if compute() cannot be instantly executed because
// another compute() call is already running, the call
// will return immediately and increase this counter
unsigned int remainingComputes = 0;
<#list ComponentHelper.getEveryBlocks(comp) as everyBlock>
    <#assign everyBlockName = ComponentHelper.getEveryBlockName(comp, everyBlock)>
  std::mutex compute${everyBlockName}Mutex;
</#list>