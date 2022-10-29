<#-- (c) https://github.com/MontiCore/monticore -->

TCLAP::ValueArg${"<"}std::string${">"} brokerHostnameArg ("","brokerHostname","Hostname (or IP address) of the MQTT broker",false,"localhost","string");
TCLAP::ValueArg${"<"}int${">"} brokerPortArg ("","brokerPort","Network port of the MQTT broker",false,1883,"int");
TCLAP::ValueArg${"<"}std::string${">"} localHostnameArg ("","localHostname","Local hostname (use host.docker.internal for Docker for Mac)",false,"localhost","string");
TCLAP::ValueArg${"<"}int${">"} localBrokerPortArg ("","localBrokerPort","Network port of the local MQTT broker",false,1883,"int");
cmd.add ( brokerHostnameArg );
cmd.add ( brokerPortArg );
cmd.add ( localHostnameArg );
cmd.add ( localBrokerPortArg );

TCLAP::SwitchArg muteMqttLogger ("", "muteMQTT", "Suppress all logs from MQTT broker", false);
cmd.add (muteMqttLogger);