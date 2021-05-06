<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#include "/template/component/helper/GeneralPreamble.ftl">

<#list comp.getOutgoingPorts() as p>
  <#assign type = ComponentHelper.getRealPortCppTypeString(p.getComponent().get(), p, config)>
  // outgoing port ${p.getName()}
  MqttPort<${type}> *${p.getName()} = new MqttPort<${type}>(this->getInstanceName () + "/${p.getName()}");
  <#if GeneratorHelper.getMqttSensorActuatorName(p, config).isPresent()>
    <#assign topicName = GeneratorHelper.getMqttSensorActuatorName(p, config).get()>
    ${p.getName()}->setSensorActuatorName ("${topicName}");
  </#if>
  this->interface.addOutPort${p.getName()?cap_first} (${p.getName()});
</#list>