<#-- (c) https://github.com/MontiCore/monticore -->
<#assign PrettyPrinter = tc.instantiate("montithings.services.prolog_generator.devicedescription.generator.ObjectDiagramToPrologConverter")>
<#list ast.ODElementList as device>
${PrettyPrinter.printODElement(device)}
</#list>