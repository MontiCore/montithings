<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#include "/template/component/helper/GeneralPreamble.ftl">

std::string modelInstanceNameIn = getModelInstanceName(this->getInstanceName());
<#list comp.getIncomingPorts() as p>
  <#assign type = TypesPrinter.getRealPortCppTypeString(p.getComponent().get(), p, config)>
  // incoming port ${p.getName()}
  MqttPort<Message<${type}>> *${p.getName()} = new MqttPort<Message<${type}>>(modelInstanceNameIn + "/${p.getName()}");
  interface.getPort${p.getName()?cap_first} ()->attach (this);
  <#if GeneratorHelper.getMqttSensorActuatorName(p, config).isPresent()>
    <#assign topicName = GeneratorHelper.getMqttSensorActuatorName(p, config).get()>
    ${p.getName()}->setSensorActuatorName ("${topicName}", true);
  </#if>
  this->interface.addInPort${p.getName()?cap_first} (${p.getName()});

  <#if !comp.isAtomic()>
    // additional outgoing port for port incoming port ${p.getName()}
    // to forward data to subcomponents
    this->interface.addOutPort${p.getName()?cap_first}(new MqttPort<Message<${type}>>(this->getInstanceName () + "/${p.getName()}", false));
  </#if>
</#list>
