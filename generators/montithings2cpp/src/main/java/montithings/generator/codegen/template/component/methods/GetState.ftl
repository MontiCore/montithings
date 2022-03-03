<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
${compname}State${generics}*
${className}${Utils.printFormalTypeParameters(comp)}::getState ()
{
    return &${Identifier.getStateName()};
}