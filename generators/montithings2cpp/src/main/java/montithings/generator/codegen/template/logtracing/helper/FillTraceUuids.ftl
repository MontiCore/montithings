<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#include "/template/component/helper/GeneralPreamble.ftl">

<#list comp.getIncomingPorts() as p>
    if(input.get${p.getName()?cap_first}().has_value()) {
        traceUUIDs.insert(std::make_pair(input.get${p.getName()?cap_first}Uuid(), "${p.getName()}"));
        isInputPresent = true;
    }
</#list>