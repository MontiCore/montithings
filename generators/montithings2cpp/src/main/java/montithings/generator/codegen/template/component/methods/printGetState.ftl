<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","className")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign Identifier = tc.instantiate("montithings.generator.codegen.util.Identifier")>
<#assign generics = Utils.printFormalTypeParameters(comp)>

${Utils.printTemplateArguments(comp)}
${compname}State${generics}*
${className}${Utils.printFormalTypeParameters(comp)}::getState ()
{
    return &${Identifier.getStateName()};
}