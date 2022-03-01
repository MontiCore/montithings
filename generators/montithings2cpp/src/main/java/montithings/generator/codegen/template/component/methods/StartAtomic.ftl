<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/Preamble.ftl">
<#include "/template/component/helper/GeneralPreamble.ftl">


${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::start(){
<#if ComponentHelper.hasInitBehavior(comp)>
  threads.push_back(std::thread{&${className}Impl${Utils.printFormalTypeParameters(comp)}::init, ${Identifier.getBehaviorImplName()}});
</#if>
threads.push_back(std::thread{&${className}${Utils.printFormalTypeParameters(comp)}::run, this});
<#list ComponentHelper.getEveryBlocks(comp) as everyBlock>
  threads.push_back(std::thread{&${className}${Utils.printFormalTypeParameters(comp)}::run${ComponentHelper.getEveryBlockName(comp, everyBlock)}, this});
</#list>
}