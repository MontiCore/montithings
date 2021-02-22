<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "statement")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Identifier = tc.instantiate("montithings.generator.codegen.util.Identifier")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign compname = comp.getName()>

<#list ComponentHelper.getPortsInGuardExpression(statement.guard) as port>
  <#if !ComponentHelper.isBatchPort(port, comp) && !ComponentHelper.portIsComparedToNoData(statement.guard, port.getName())>
    ${Identifier.getInputName()}.get${port.getName()?cap_first}()
  <#else>
    true // presence of value on port ${port.getName()} not checked as it is compared to NoData
  </#if>
  <#sep>&&</#sep>
</#list>
<#if ComponentHelper.getPortsInGuardExpression(statement.guard)?size == 0>
  true // presence of value on ports not checked as they are not used in precondition
</#if>
&& !(${Utils.printExpression(statement.guard)})