<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","className")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>

${Utils.printTemplateArguments(comp)}
json
${className}${Utils.printFormalTypeParameters(comp)}::getSerializedState ()
{
    return state.serializeState ();
}