<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#include "/template/component/helper/GeneralPreamble.ftl">

<#list comp.getOutgoingPorts() as p>
  //comp->getInterface()->getPort${p.getName()?cap_first}()->attach(this);
</#list>


<#list comp.getOutgoingPorts() as p>
  <#list comp.getAstNode().getConnectors() as connector>
    <#list connector.getTargetList() as target>
      <#if target.getQName() == p.getName()>
        comp->getInterface()->getPort${p.getName()?cap_first}()->attach(this);
      </#if>
    </#list>
  </#list>
</#list>
