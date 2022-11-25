<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#include "/template/component/helper/GeneralPreamble.ftl">


std::string modelInstanceNameOut = getModelInstanceName(this->getInstanceName());
<#list comp.getOutgoingPorts() as p>
  <#assign type = TypesPrinter.getRealPortCppTypeString(comp, p, config)>

  // outgoing port ${p.getName()}

  ${p.getName()} = new MqttPort<Message<${type}>>(modelInstanceNameOut + "/${p.getName()}",
  std::unique_ptr<${serializerName}<Message<${type}>>>{new ${serializerName}<Message<${type}>>{}}
  ,false, mqttClientInstance, mqttClientLocalInstance);
  <#if GeneratorHelper.getMqttSensorActuatorName(p, config).isPresent()>
    <#assign sensorActuatorType = GeneratorHelper.getMqttSensorActuatorName(p, config).get()>
    std::string sensorActuatorRequestTopic${p.getName()?cap_first} = "/sensorActuator/request/" + this->getInstanceName() + ".${p.getName()}";
    mqttClientLocalInstance->subscribe ("/sensorActuator/response/" + this->getInstanceName() + ".${p.getName()}");
    mqttClientLocalInstance->publishRetainedMessage (sensorActuatorRequestTopic${p.getName()?cap_first}, "{\"type\":\"${sensorActuatorType}\"}");
  </#if>
  <#if !comp.isAtomic()>
    <#--
      Only generate this for composed components
      If used in atomic components you get duplicated messages
      because each InPort get the messages from compute() via setNextValue()
      and each of them will trigger the outgoing ports (-> duplicated messages)
    -->
    this->interface.addInPort${p.getName()?cap_first} (new MqttPort<Message<${type}>>(this->getInstanceName () + "/${p.getName()}",
    std::unique_ptr<${serializerName}<Message<${type}>>>{new ${serializerName}<Message<${type}>>{}}
    ,true,mqttClientInstance, mqttClientLocalInstance));
  </#if>
  this->interface.addOutPort${p.getName()?cap_first} (${p.getName()});
  <#if needsProtobuf>
    <#assign protoname = p.getName() + "_protobuf">
    // incoming protobuf port ${protoname}
    ${protoname} = new MqttPort<Message<${type}>>(modelInstanceNameOut + "/${p.getName()}",
    std::unique_ptr<${serializerName}<Message<${type}>>>{new ${serializerName}<Message<${type}>>{}}
    ,true , mqttClientInstance, mqttClientLocalInstance, "/protobuf/");
  </#if>
</#list>