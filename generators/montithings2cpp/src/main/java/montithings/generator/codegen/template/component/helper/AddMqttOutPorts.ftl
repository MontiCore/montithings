<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#include "/template/component/helper/GeneralPreamble.ftl">


std::string modelInstanceNameOut = getModelInstanceName(this->getInstanceName());
<#list comp.getOutgoingPorts() as p>
  <#assign type = TypesPrinter.getRealPortCppTypeString(comp, p, config)>

  // outgoing port ${p.getName()}

  ${p.getName()} = new MqttPort<Message<${type}>>(modelInstanceNameOut + "/${p.getName()}", true, mqttClientInstance);
  <#if GeneratorHelper.getMqttSensorActuatorName(p, config).isPresent()>
    <#assign sensorActuatorType = GeneratorHelper.getMqttSensorActuatorName(p, config).get()>

    std::vector< std::string > sensorActuatorTopics${p.getName()?cap_first} = sensorActuatorTypes["${sensorActuatorType}"];
    std::string topicName = sensorActuatorTopics${p.getName()?cap_first}[0];

    std::this_thread::sleep_for(std::chrono::milliseconds(rand() % 50));
    ${p.getName()}->setSensorActuatorName (topicName, false);
    std::string sensorActuatorConfigTopic = "/sensorActuator/config/" + topicName;
    mqttClientLocalInstance->subscribe (sensorActuatorConfigTopic);

    std::future<void> keepAliveFuture${p.getName()?cap_first} = exitSignal${p.getName()?cap_first}.get_future();
    th${p.getName()?cap_first} = std::thread(&${className}::sendKeepAlive, this, sensorActuatorConfigTopic, "${p.getName()}", std::move(keepAliveFuture${p.getName()?cap_first}));

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
  this->interface.addOutPort${p.getName()?cap_first} (${p.getName()});
</#list>