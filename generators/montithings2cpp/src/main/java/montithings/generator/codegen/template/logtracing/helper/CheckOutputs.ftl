<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#include "/template/component/helper/GeneralPreamble.ftl">

<#list comp.getOutgoingPorts() as p>
    if(comp->getInterface()->getPort${p.getName()?cap_first}()->getOutport()->hasValue(this->uuid)) {
        isOutputPresent = true;
    }
</#list>