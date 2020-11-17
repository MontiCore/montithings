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
  while(!MqttClient::instance()->isConnected());
</#if>

${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.name} cmp (argv[1]);
<#if config.getSplittingMode().toString() != "OFF" && config.getMessageBroker().toString() == "OFF">
  ${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.name}Manager manager (&cmp, argv[2], argv[3]);
  manager.initializePorts ();
  <#if comp.isDecomposed()>
    manager.searchSubcomponents ();
  </#if>
</#if>