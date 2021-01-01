<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","compname","className")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Identifier = tc.instantiate("montithings.generator.codegen.util.Identifier")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign generics = Utils.printFormalTypeParameters(comp)>
<#if ComponentHelper.hasBehavior(comp)>
    ${Utils.printTemplateArguments(comp)}
    ${compname}Result${generics} ${className}${generics}::getInitialValues(){
    return {};
    }

    ${Utils.printTemplateArguments(comp)}
    ${compname}Result${generics} ${className}${generics}::compute(${compname}Input${generics}
    ${Identifier.getInputName()}){
    ${compname}Result${generics} result;
    ${ComponentHelper.printStatementBehavior(comp)}
    return result;
    }
</#if>

${Utils.printTemplateArguments(comp)}
void ${className}${generics}::setInstanceName (const std::string &instanceName)
{
this->instanceName = instanceName;
}

${tc.includeArgs("template.behavior.implementation.printStateMethods", [comp, className])}