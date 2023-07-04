<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#include "/template/Preamble.ftl">

<#if brokerIsMQTT>
  <#if ComponentHelper.isDSLComponent(comp,config)>
    MqttClient* mqttClientInstance = MqttClient::instance(brokerHostnameArg.getValue (), brokerPortArg.getValue (), (instanceNameArg.getValue() + "_CPP").c_str());
  <#else>
    MqttClient* mqttClientInstance = MqttClient::instance(brokerHostnameArg.getValue (), brokerPortArg.getValue (), (instanceNameArg.getValue()).c_str());
  </#if>
  MqttClient* mqttClientLocalInstance;
  if((brokerHostnameArg.getValue() == localHostnameArg.getValue())){
    mqttClientLocalInstance = mqttClientInstance;
  } else {
    mqttClientLocalInstance = MqttClient::localInstance(localHostnameArg.getValue (), localBrokerPortArg.getValue ());
  }


  // Wait for initial connection
  while(!mqttClientInstance->isConnected())
  ;
  while(!mqttClientLocalInstance->isConnected())
  ;

  <#if comp.getParameters()?size gt 0 || ComponentHelper.getSIUnitPortNames(comp)?size gt 0 || config.getTypeArguments(comp)?size gt 0>
    MqttConfigRequester configRequester;
    configRequester.requestConfig(getModelInstanceName(instanceNameArg.getValue ()));

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
