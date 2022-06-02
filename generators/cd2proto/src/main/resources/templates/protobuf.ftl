<#-- (c) https://github.com/MontiCore/monticore -->
// (c) https://github.com/MontiCore/monticore
${tc.signature("types")}

syntax = "proto3";

<#-- TODO: nested generic types, e.g. List<List<T>> -->
<#-- TODO: Class hierarchies -->
<#-- TODO: associations -->

<#-- TODO: Write a helper in Java that maps this stuff properly -->
<#function java2cppTypeString type>
    <#assign output = type>
    <#assign output = output?replace("([^<]*)\\[]", "std::vector<$1>")>
    <#assign output = output?replace("String", "string")>
    <#assign output = output?replace("Integer", "int32")>
    <#assign output = output?replace("Long", "long")>
    <#assign output = output?replace("Map", "map")>
    <#assign output = output?replace("Set", "")>
    <#assign output = output?replace("List<(.*)>", "repeated $1")>
    <#assign output = output?replace("Boolean", "bool")>
    <#assign output = output?replace("boolean", "bool")>
    <#assign output = output?replace("Character", "char")>
    <#assign output = output?replace("Double", "double")>
    <#assign output = output?replace("Float", "float")>
    <#assign output = output?replace("InPort", "PortLink")>
    <#assign output = output?replace("OutPort", "PortLink")>
    <#return output>
</#function>

<#list types as type>
message ${type.name} {
    <#list type.fieldList as field>
         ${java2cppTypeString(field.type.typeInfo.name)} ${field.name} = ${field?counter};
    </#list>
}

</#list>