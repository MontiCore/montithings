<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

<#if config.getMessageBroker().toString() == "MQTT">
  if (argc == 4) { MqttClient::instance(argv[2], std::stoi(argv[3])); }
  else
  {
  std::cerr << "WARNING: No MQTT hostname and port provided as argument. Defaulting to localhost:1883." << std::endl;
  MqttClient::instance();
  }
  // Wait for initial connection
  while(!MqttClient::instance()->isConnected())
  ;

  <#if comp.getParameters()?size gt 0>
    MqttConfigRequester configRequester;
    configRequester.requestConfig(argv[1]);

    // Wait for initial connection
    while (!configRequester.hasReceivedConfig())
    ;

    json config = configRequester.getConfig();
    <#list comp.getParameters() as variable>
      <#assign typeName = ComponentHelper.printCPPTypeName(variable.getType())>
      ${typeName} ${variable.getName()} = jsonToData${"<"}${typeName}${">"}(config["${variable.getName()}"]);
    </#list>
    <#if config.getTypeArguments(comp)?size gt 0>
      std::string _typeArgs = jsonToData${"<"}std::string${">"} (config["_typeArgs"]);
    </#if>
  </#if>
</#if>

<#-- NO TEMPLATE ARGUMENTS -->
<#if config.getTypeArguments(comp)?size == 0>
    ${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.name} cmp (argv[1]
    <#if comp.getParameters()?size gt 0>,</#if>
    <#list comp.getParameters() as variable>
        ${variable.getName()} <#sep>,</#sep>
    </#list>
  );
    ${tc.includeArgs("template.deploy.InitDDSParticipant", [comp, config])}
    ${tc.includeArgs("template.deploy.InitCommunicationManager", [comp, config])}

  cmp.setUp(
  <#if ComponentHelper.isTimesync(comp)>
    TIMESYNC
  <#else>
    EVENTBASED
  </#if>
  );
  cmp.init();
  <#if !ComponentHelper.isTimesync(comp)>
    cmp.start();
  </#if>
  ${tc.includeArgs("template.deploy.KeepAlive", [comp, config])}

<#else>
<#-- WITH TEMPLATE ARGUMENTS -->
    <#list config.getTypeArguments(comp) as typeArguments>
        <#if typeArguments?counter gt 1>else</#if>
      if (_typeArgs == "${typeArguments}")
      {
        ${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.name}${"<"}${typeArguments}${">"} cmp (argv[1]
        <#if comp.getParameters()?size gt 0>,</#if>
        <#list comp.getParameters() as variable>
            ${variable.getName()} <#sep>,</#sep>
        </#list>
      );
        ${tc.includeArgs("template.deploy.InitDDSParticipant", [comp, config])}
    ${tc.includeArgs("template.deploy.InitCommunicationManager", [comp, config])}


    cmp.setUp(
    <#if ComponentHelper.isTimesync(comp)>
      TIMESYNC
    <#else>
      EVENTBASED
    </#if>
    );
    cmp.init();
    <#if !ComponentHelper.isTimesync(comp)>
      cmp.start();
    </#if>
    ${tc.includeArgs("template.deploy.KeepAlive", [comp, config])}
  }

  </#list>
</#if>