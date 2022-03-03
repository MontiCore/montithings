<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
${compname}Impl${generics}*
${className}${Utils.printFormalTypeParameters(comp)}::getImpl ()
{
    return &${Identifier.getBehaviorImplName()};
}