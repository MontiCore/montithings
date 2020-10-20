${tc.signature("comp","compname","isTOP")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Identifier = tc.instantiate("montithings.generator.codegen.util.Identifier")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign generics = Utils.printFormalTypeParameters(comp)>
<#if ComponentHelper.hasBehavior(comp)>
    ${Utils.printTemplateArguments(comp)}
    ${compname}Result${generics} ${compname}Impl<#if isTOP>TOP</#if>${generics}::getInitialValues(){
    return {};
    }

    ${Utils.printTemplateArguments(comp)}
    ${compname}Result${generics} ${compname}Impl<#if isTOP>TOP</#if>${generics}::compute(${compname}Input${generics}
    ${Identifier.getInputName()}){
    ${compname}Result${generics} result;
    ${ComponentHelper.printStatementBehavior(comp)}
    return result;
    }
</#if>
