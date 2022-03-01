<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#include "/template/Preamble.ftl">
<#include "/template/component/helper/GeneralPreamble.ftl">

std::string modelInstanceNameIn = getModelInstanceName(this->getInstanceName());
<#list comp.getIncomingPorts() as p>
  <#assign type = TypesPrinter.getRealPortCppTypeString(p.getComponent().get(), p, config)>
  // incoming port ${p.getName()}

  ${p.getName()} = new MqttPort<Message<${type}>>(modelInstanceNameIn + "/${p.getName()}", true, mqttClientInstance, mqttClientLocalInstance);
  interface.getPort${p.getName()?cap_first} ()->attach (this);
  <#if GeneratorHelper.getMqttSensorActuatorName(p, config).isPresent()>
    <#assign sensorActuatorType = GeneratorHelper.getMqttSensorActuatorName(p, config).get()>
    std::vector< std::string > sensorActuatorTopics${p.getName()?cap_first} = sensorActuatorTypes["${sensorActuatorType}"];
    std::string topicName = sensorActuatorTopics${p.getName()?cap_first}[0];

    std::this_thread::sleep_for(std::chrono::milliseconds(rand() % 50));
    ${p.getName()}->setSensorActuatorName (topicName, true);
    std::string sensorActuatorConfigTopic = "/sensorActuator/config/" + topicName;
    mqttClientLocalInstance->subscribe (sensorActuatorConfigTopic);

    std::future<void> keepAliveFuture${p.getName()?cap_first} = exitSignal${p.getName()?cap_first}.get_future();
    th${p.getName()?cap_first} = std::thread(&${className}::sendKeepAlive, this, sensorActuatorConfigTopic, "${p.getName()}", std::move(keepAliveFuture${p.getName()?cap_first}));
  </#if>
  this->interface.addInPort${p.getName()?cap_first} (${p.getName()});

  <#if !comp.isAtomic()>
    // additional outgoing port for port incoming port ${p.getName()}
    // to forward data to subcomponents
    this->interface.addOutPort${p.getName()?cap_first}(new MqttPort<Message<${type}>>(this->getInstanceName () + "/${p.getName()}", false, mqttClientInstance, mqttClientLocalInstance));
  </#if>
</#list>
