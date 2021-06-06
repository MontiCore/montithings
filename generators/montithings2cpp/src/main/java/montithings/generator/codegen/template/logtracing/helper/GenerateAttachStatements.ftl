<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#include "/template/component/helper/GeneralPreamble.ftl">

<#list comp.getPorts() as p>
    comp->getInterface()->getPort${p.getName()?cap_first}()->attach(this);
</#list>
<#--
<#list comp.getOutgoingPorts() as p>
    comp->getInterface()->getPort${p.getName()?cap_first}()->getOutport()->attach(this);
</#list>
-->