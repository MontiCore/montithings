<#-- @ftlvariable name="types" type="java.util.List<de.monticore.cdbasis._symboltable.CDTypeSymbol>" -->
<#-- @ftlvariable name="TypeHelper" type="montithings.generator.cd2proto.helper.TypeHelper" -->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->

<#-- (c) https://github.com/MontiCore/monticore -->
// (c) https://github.com/MontiCore/monticore
${tc.signature("types", "TypeHelper", "package")}

syntax = "proto3";
<#-- TODO: associations -->

package ${package};

<#list types as type>
    <#if type.isInterface>
        <#-- We don't care for interfaces as they do not have data. -->
        <#continue >

    <#elseif type.isClass>

<#assign nextFieldNumber=1>
message ${type.name} {
    <#if type.presentSuperClass>
        ${type.superClass.typeInfo.name} super = ${nextFieldNumber};
        <#assign nextFieldNumber+=1>
    </#if>

    <#list type.fieldList as field>
    ${TypeHelper.translate(field.getType())} ${field.name} = ${nextFieldNumber};
        <#assign nextFieldNumber+=1>
    </#list>
}

    <#elseif type.isEnum>
message ${type.name} {
        enum Values {
        <#list type.fieldList as field>
            ${field.name} = ${field?counter-1};
        </#list>
        }
}
    </#if>

</#list>

${TypeHelper.getNestedListHelper().generateWrappers()}