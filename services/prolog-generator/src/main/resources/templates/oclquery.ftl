<#-- (c) https://github.com/MontiCore/monticore -->
<#assign PrettyPrinter = tc.instantiate("montithings.services.prolog_generator.oclquery.generator.OCLToPrologConverter")>
<#assign Utils = tc.instantiate("montithings.services.prolog_generator.Utils")>

<#list PrettyPrinter.getNameExpressions(ast) as name>${name}(__X, ${Utils.capitalize(name)}), </#list>${PrettyPrinter.printOCLQuery(ast)}.
