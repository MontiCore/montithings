<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if config.getMessageBroker().toString() == "MQTT">
  MqttClient* mqttClientInstance = MqttClient::instance(brokerHostnameArg.getValue (), brokerPortArg.getValue ());
  MqttClient* mqttClientLocalInstance;
  if((brokerHostnameArg.getValue() == "localhost" || brokerHostnameArg.getValue() == "host.docker.internal")&& brokerPortArg.getValue() == 1883){
    mqttClientLocalInstance = mqttClientInstance;
  } else {
    mqttClientLocalInstance = MqttClient::instance(<#if config.getSplittingMode().toString() == "DISTRIBUTED">"host.docker.internal"<#else>"localhost"</#if>, 1883);
  }

  // Wait for initial connection
  while(!mqttClientInstance->isConnected());
  while(!mqttClientLocalInstance->isConnected());

  <#if comp.getParameters()?size gt 0 || ComponentHelper.getSIUnitPortNames(comp)?size gt 0 || config.getTypeArguments(comp)?size gt 0>
    MqttConfigRequester configRequester;
    configRequester.requestConfig(instanceNameArg.getValue ());

    // Wait for initial connection
    while (!configRequester.hasReceivedConfig())
    ;

    json config = configRequester.getConfig();
    <#list comp.getParameters() as variable>
      <#assign typeName = TypesPrinter.printCPPTypeName(variable.getType())>
      ${typeName} ${variable.getName()} = jsonToData${"<"}${typeName}${">"}(config["${variable.getName()}"]);
    </#list>
    <#list ComponentHelper.getSIUnitPortNames(comp) as portName>
      double ${portName}ConversionFactor = jsonToData${"<"}double${">"}(config["${portName}ConversionFactor"]);
    </#list>
    <#if config.getTypeArguments(comp)?size gt 0>
      std::string _typeArgs = jsonToData${"<"}std::string${">"} (config["_typeArgs"]);
    </#if>
  </#if>
</#if>