<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

TCLAP::ValueArg${"<"}std::string${">"} brokerHostnameArg ("","brokerHostname","Hostname (or IP address) of the MQTT broker",false,"localhost","string");
TCLAP::ValueArg${"<"}int${">"} brokerPortArg ("","brokerPort","Network port of the MQTT broker",false,1883,"int");
cmd.add ( brokerHostnameArg );
cmd.add ( brokerPortArg );