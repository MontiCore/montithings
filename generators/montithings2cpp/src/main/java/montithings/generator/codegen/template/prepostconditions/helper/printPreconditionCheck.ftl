<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "statement")}
<#include "/template/Preamble.ftl">

<#list ComponentHelper.getPortsInGuardExpression(statement.guard) as port>
  <#if dummyName14>
    ${Identifier.getInputName()}.get${port.getName()?cap_first}()
  <#else>
    true // presence of value on port ${port.getName()} not checked as it is compared to NoData
  </#if>
  <#sep>&&</#sep>
</#list>
<#if dummyName15>
  true // presence of value on ports not checked as they are not used in precondition
</#if>
&& !(${Utils.printExpression(statement.guard)})