<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#include "/template/component/helper/GeneralPreamble.ftl">

<#list comp.getIncomingPorts() as p>
  if(${Identifier.getInputName()}.get${p.getName()?cap_first}().has_value()) {
    traceUUIDs.insert(std::make_pair(${Identifier.getInputName()}.get${p.getName()?cap_first}Uuid(), "${p.getName()}"));
    isInputPresent = true;
  }
</#list>

// include ports which are target ports of subcomponents as well
<#list comp.getAllOutgoingPorts() as port>
  <#if !comp.isAtomic()>
  <#list comp.getAstNode().getConnectors() as connector>
    <#list connector.getTargetList() as target>
      <#if target.getQName() == port.getName()>
        if (comp->getInterface()->getPort${port.getName()?cap_first}()->hasValue(this->uuid)) {
          ${Identifier.getInputName()}.set${port.getName()?cap_first}(interface->getPort${port.getName()?cap_first}()->getCurrentValue(this->uuid).value());
        }
        if (input.get${port.getName()?cap_first}().has_value()) {
          subCompOutputForwards.push_back(input.get${port.getName()?cap_first}Uuid());
          traceUUIDs.insert(std::make_pair(input.get${port.getName()?cap_first}Uuid(), "${port.getName()}"));
          isOutputPresent = true;
        }
      </#if>
    </#list>
  </#list>
  </#if>
</#list>