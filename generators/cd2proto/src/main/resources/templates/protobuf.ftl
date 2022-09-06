<#-- @ftlvariable name="types" type="java.util.List<de.monticore.cdbasis._symboltable.CDTypeSymbol>" -->
<#-- @ftlvariable name="TypeHelper" type="montithings.generator.cd2proto.helper.TypeHelper" -->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->

<#-- (c) https://github.com/MontiCore/monticore -->
// (c) https://github.com/MontiCore/monticore
${tc.signature("types", "TypeHelper", "package", "AssociationHelper")}

syntax = "proto3";

package ${package};

<#list types as type>
    <#if type.isInterface>
        <#continue ><#-- We don't care for interfaces as they do not have data. -->
    <#elseif type.isClass><#assign nextFieldNumber=1>
message ${type.name} {
    <#if type.presentSuperClass>
    // Parent class
    ${type.superClass.typeInfo.name} super = ${nextFieldNumber};
        <#assign nextFieldNumber+=1>
    </#if>
    <#list type.fieldList as field>
    <#if field?is_first>
    // Fields
    </#if>
    ${TypeHelper.translate(field.getType())} ${field.name} = ${nextFieldNumber};
        <#assign nextFieldNumber+=1>
    </#list>
    <#list AssociationHelper.getAssociations(ast, type) as association>
    <#if association?is_first>
    // Associations
    </#if>
        <#assign isRepeated=AssociationHelper.getOtherSide(association, type).CDCardinality.mult || AssociationHelper.getOtherSide(association, type).CDCardinality.opt>
    <#if isRepeated>repeated </#if>${AssociationHelper.getOtherSideTypeName(association, type)} ${AssociationHelper.getDerivedName(association, type)} = ${nextFieldNumber};
    <#assign nextFieldNumber+=1>
    </#list>
}

    <#elseif type.isEnum>
enum ${type.name} {
    <#list type.fieldList as field>
    ${field.name} = ${field?counter-1};
    </#list>
}
    </#if>

</#list>

${TypeHelper.getNestedListHelper().generateWrappers()}