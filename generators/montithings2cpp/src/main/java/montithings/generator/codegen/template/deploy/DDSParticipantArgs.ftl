<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

TCLAP::ValueArg${"<"}std::string${">"} dcpsConfigArg ("","DCPSConfigFile","Config file for DCPS (e.g. dcpsconfig.ini)",true,"","string");
cmd.add ( dcpsConfigArg );

<#if config.getSplittingMode().toString() == "DISTRIBUTED">
  TCLAP::ValueArg${"<"}std::string${">"} dcpsInfoRepoArg ("","DCPSInfoRepo","Hostname and network port of the DCPSInfoRepo (e.g. localhost:12345)",false,"","string");
  cmd.add ( dcpsInfoRepoArg );
</#if>

TCLAP::SwitchArg muteDdsLogger ("", "muteDDS", "Suppress all logs from DDS broker", false);
cmd.add (muteDdsLogger);