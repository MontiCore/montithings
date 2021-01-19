<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

<#if config.getMessageBroker().toString() == "MQTT">
  MqttClient::instance(brokerHostnameArg.getValue (), brokerPortArg.getValue ());

  // Wait for initial connection
  while(!MqttClient::instance()->isConnected())
  ;

  <#if comp.getParameters()?size gt 0>
    MqttConfigRequester configRequester;
    configRequester.requestConfig(instanceNameArg.getValue ());

    // Wait for initial connection
    while (!configRequester.hasReceivedConfig())
    ;

    json config = configRequester.getConfig();
    <#list comp.getParameters() as variable>
      <#assign typeName = ComponentHelper.printCPPTypeName(variable.getType())>
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