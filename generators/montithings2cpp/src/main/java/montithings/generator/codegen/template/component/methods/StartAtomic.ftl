<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">


${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::start(){
threads.push_back(std::thread{&${className}${Utils.printFormalTypeParameters(comp)}::run, this});
<#list ComponentHelper.getEveryBlocks(comp) as everyBlock>
  threads.push_back(std::thread{&${className}${Utils.printFormalTypeParameters(comp)}::run${ComponentHelper.getEveryBlockName(comp, everyBlock)}, this});
</#list>
}