<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#include "/template/Preamble.ftl">
<#include "/template/component/helper/GeneralPreamble.ftl">


std::string modelInstanceNameOut = getModelInstanceName(this->getInstanceName());
<#list comp.getOutgoingPorts() as p>
  <#assign type = TypesPrinter.getRealPortCppTypeString(comp, p, config)>

  // outgoing port ${p.getName()}

  ${p.getName()} = new MqttPort<Message<${type}>>(modelInstanceNameOut + "/${p.getName()}", false, mqttClientInstance, mqttClientLocalInstance);
  <#if GeneratorHelper.getMqttSensorActuatorName(p, config).isPresent()>
    <#assign sensorActuatorType = GeneratorHelper.getMqttSensorActuatorName(p, config).get()>

    std::vector< std::string > sensorActuatorTopics${p.getName()?cap_first} = sensorActuatorTypes["${sensorActuatorType}"];
    std::string topicname${p.getName()?cap_first} = sensorActuatorTopics${p.getName()?cap_first}[0];

    std::this_thread::sleep_for(std::chrono::milliseconds(rand() % 50));
    ${p.getName()}->setSensorActuatorName (topicname${p.getName()?cap_first}, false);
    std::string sensorActuatorConfigTopic${p.getName()?cap_first} = "/sensorActuator/config/" + topicname${p.getName()?cap_first};
    mqttClientLocalInstance->subscribe (sensorActuatorConfigTopic${p.getName()?cap_first});

    std::future<void> keepAliveFuture${p.getName()?cap_first} = exitSignal${p.getName()?cap_first}.get_future();
    th${p.getName()?cap_first} = std::thread(&${className}::sendKeepAlive, this, sensorActuatorConfigTopic${p.getName()?cap_first}, "${p.getName()}", std::move(keepAliveFuture${p.getName()?cap_first}));

  </#if>
  <#if !comp.isAtomic()>
    <#--
      Only generate this for composed components
      If used in atomic components you get duplicated messages
      because each InPort get the messages from compute() via setNextValue()
      and each of them will trigger the outgoing ports (-> duplicated messages)
    -->
    this->interface.addInPort${p.getName()?cap_first} (new MqttPort<Message<${type}>>(this->getInstanceName () + "/${p.getName()}", true,mqttClientInstance, mqttClientLocalInstance));
  </#if>
  this->interface.addOutPort${p.getName()?cap_first} (${p.getName()});
</#list>