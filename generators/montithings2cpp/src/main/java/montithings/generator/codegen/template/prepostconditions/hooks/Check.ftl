<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "preOrPost")}
<#assign compname = comp.getName()>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign Identifier = tc.instantiate("montithings.generator.codegen.util.Identifier")>
for (auto ${preOrPost} : ${preOrPost}conditions)
{
${preOrPost}->apply (${Identifier.getStateName()}, ${Identifier.getInputName()}
<#if preOrPost == "post">, ${Identifier.getResultName()}</#if>, ${Identifier.getStateName()}__at__pre);
}