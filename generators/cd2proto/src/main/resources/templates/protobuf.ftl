<#-- @ftlvariable name="types" type="java.util.List<de.monticore.cdbasis._symboltable.CDTypeSymbol>" -->
<#-- @ftlvariable name="TypeHelper" type="montithings.generator.cd2proto.helper.TypeHelper" -->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->

<#-- (c) https://github.com/MontiCore/monticore -->
// (c) https://github.com/MontiCore/monticore
${tc.signature("types", "TypeHelper")}

syntax = "proto3";
<#-- TODO: Class hierarchies -->
<#-- TODO: associations -->

<#list types as type>
message ${type.name} {
    <#list type.fieldList as field>
    ${TypeHelper.translate(field.getType())} ${field.name} = ${field?counter};
    </#list>
}
</#list>

${TypeHelper.getNestedListHelper().generateWrappers()}