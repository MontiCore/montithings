<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

TCLAP::ValueArg${"<"}std::string${">"} managementPortArg ("","managementPort","Network port for management traffic",true,"","string");
TCLAP::ValueArg${"<"}std::string${">"} dataPortArg ("","dataPort","Network port for data traffic",true,"","string");
cmd.add ( managementPortArg );
cmd.add ( dataPortArg );