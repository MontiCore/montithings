<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">
<#include "/template/Preamble.ftl">
<#assign generics = Utils.printFormalTypeParameters(comp)>

${Utils.printTemplateArguments(comp)}
${compname}State${generics}*
${className}${Utils.printFormalTypeParameters(comp)}::getState ()
{
    return &${Identifier.getStateName()};
}