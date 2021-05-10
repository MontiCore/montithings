<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#include "/template/component/helper/GeneralPreamble.ftl">


std::vector< std::thread > threads;

std::mutex computeMutex;
<#list ComponentHelper.getEveryBlocks(comp) as everyBlock>
    <#assign everyBlockName = ComponentHelper.getEveryBlockName(comp, everyBlock)>
  std::mutex compute${everyBlockName}Mutex;
</#list>