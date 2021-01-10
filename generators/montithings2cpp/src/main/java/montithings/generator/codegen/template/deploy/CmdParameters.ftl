<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

TCLAP::ValueArg${"<"}std::string${">"} instanceNameArg ("n", "name","Fully qualified instance name of the component",true,"","string");
cmd.add ( instanceNameArg );

<#if config.getSplittingMode().toString() == "LOCAL" && config.getMessageBroker().toString() == "OFF">
  ${tc.includeArgs("template.deploy.CommunicationManagerArgs", [comp, config])}
<#elseif config.getMessageBroker().toString() == "MQTT">
  ${tc.includeArgs("template.deploy.MqttArgs", [comp, config])}
<#elseif config.getMessageBroker().toString() == "DDS">
  ${tc.includeArgs("template.deploy.DDSParticipantArgs", [comp, config])}
</#if>>

cmd.parse ( argc, argv );