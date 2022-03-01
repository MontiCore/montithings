<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">
<#include "/template/TcPreamble.ftl">
<#assign generics = Utils.printFormalTypeParameters(comp)>

${Utils.printTemplateArguments(comp)}
${compname}Impl${generics}*
${className}${Utils.printFormalTypeParameters(comp)}::getImpl ()
{
    return &${Identifier.getBehaviorImplName()};
}