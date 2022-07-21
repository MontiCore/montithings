<#-- (c) https://github.com/MontiCore/monticore -->
<#assign PrettyPrinter = tc.instantiate("montithings.services.prolog_generator.oclquery.generator.OCLToPrologConverter")>
<#assign Utils = tc.instantiate("montithings.services.prolog_generator.Utils")>

<#list PrettyPrinter.getNameExpressions(ast) as name>${Utils.getFactName(name)}(__${Utils.getNameOfOuterScope(name)}, ${Utils.capitalize(name)}), </#list>${PrettyPrinter.printOCLQuery(ast)}.
