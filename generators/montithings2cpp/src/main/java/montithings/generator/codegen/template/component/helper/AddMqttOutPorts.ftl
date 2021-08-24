<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#include "/template/component/helper/GeneralPreamble.ftl">

std::string modelInstanceNameOut = getModelInstanceName(this->getInstanceName());
<#list comp.getOutgoingPorts() as p>
  <#assign type = TypesPrinter.getRealPortCppTypeString(comp, p, config)>

  // outgoing port ${p.getName()}
  MqttPort<Message<${type}>> *${p.getName()} = new MqttPort<Message<${type}>>(this->getInstanceName () + "/${p.getName()}");
  <#if GeneratorHelper.getMqttSensorActuatorName(p, config).isPresent()>
    <#assign topicName = GeneratorHelper.getMqttSensorActuatorName(p, config).get()>
    ${p.getName()}->setSensorActuatorName ("${topicName}", false);
  </#if>
  <#if !comp.isAtomic()>
    <#--
      Only generate this for composed components
      If used in atomic components you get duplicated messages
      because each InPort get the messages from compute() via setNextValue()
      and each of them will trigger the outgoing ports (-> duplicated messages)
    -->
    this->interface.addInPort${p.getName()?cap_first} (${p.getName()});
  </#if>
  this->interface.addOutPort${p.getName()?cap_first} (new MqttPort<Message<${type}>>(modelInstanceNameOut + "/${p.getName()}", false));
</#list>