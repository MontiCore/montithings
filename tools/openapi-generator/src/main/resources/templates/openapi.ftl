<#-- (c) https://github.com/MontiCore/monticore -->
<#assign PrettyPrinter = tc.instantiate("montithings.services.openapi_generator.openapi.generator.OpenAPIToMTConverter")>
// (c) https://github.com/MontiCore/monticore

package openapi;

interface component ${PrettyPrinter.getComponentTypeName(ast)} {
<#list PrettyPrinter.getIncomingPortNames(ast) as portName>
  port in String ${portName};
</#list>
<#list PrettyPrinter.getStateVariables(ast) as variableName>
  String ${variableName} = "";
</#list>
}