<#-- (c) https://github.com/MontiCore/monticore -->
// (c) https://github.com/MontiCore/monticore
${tc.signature("types")}

<#list types as type>
message ${type.name} {
}

</#list>