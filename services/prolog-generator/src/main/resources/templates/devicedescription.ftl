<#-- (c) https://github.com/MontiCore/monticore -->
<#assign PrettyPrinter = tc.instantiate("montithings.services.prolog_generator.devicedescription.generator.ObjectDiagramToPrologConverter")>
property("device", 1, "${ast.name}").
<#list ast.ODElementList as device>
    ${PrettyPrinter.printODElement(device)}
</#list>