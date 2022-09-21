<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#include "/template/component/helper/GeneralPreamble.ftl">

std::string modelInstanceNameIn = getModelInstanceName(this->getInstanceName());
<#list comp.getIncomingPorts() as p>
  <#assign type = TypesPrinter.getRealPortCppTypeString(p.getComponent().get(), p, config)>
  // incoming port ${p.getName()}

  ${p.getName()} = new MqttPort<Message<${type}>>(modelInstanceNameIn + "/${p.getName()}",
  std::unique_ptr<${serializerName}<Message<${type}>>>{new ${serializerName}<Message<${type}>>{}}
  ,true, mqttClientInstance, mqttClientLocalInstance);
  interface.getPort${p.getName()?cap_first} ()->attach (this);
  <#if GeneratorHelper.getMqttSensorActuatorName(p, config).isPresent()>
    <#assign sensorActuatorType = GeneratorHelper.getMqttSensorActuatorName(p, config).get()>
    std::string sensorActuatorRequestTopic${p.getName()?cap_first} = "/sensorActuator/request/" + this->getInstanceName() + ".${p.getName()}";
    mqttClientLocalInstance->subscribe ("/sensorActuator/response/" + this->getInstanceName() + ".${p.getName()}");
    mqttClientLocalInstance->publishRetainedMessage (sensorActuatorRequestTopic${p.getName()?cap_first}, "{\"type\":\"${sensorActuatorType}\"}");
  </#if>
  this->interface.addInPort${p.getName()?cap_first} (${p.getName()});

  <#if !comp.isAtomic()>
    // additional outgoing port for port incoming port ${p.getName()}
    // to forward data to subcomponents
    this->interface.addOutPort${p.getName()?cap_first}(new MqttPort<Message<${type}>>(this->getInstanceName () + "/${p.getName()}",
    std::unique_ptr<${serializerName}<Message<${type}>>>{new ${serializerName}<Message<${type}>>{}}
    ,false, mqttClientInstance, mqttClientLocalInstance));
  </#if>
</#list>
