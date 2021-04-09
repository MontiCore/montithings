<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/util/comm/helper/GeneralPreamble.ftl">


${className}::${className}
(${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.getName()} *comp,
std::string managementPort, std::string communicationPort)
: comp (comp), managementPort (managementPort), communicationPort (communicationPort)
{
comm = new ManagementCommunication ();
comm->init(managementPort);
comm->registerMessageProcessor (this);
portConfigFilePath = "ports/" + comp->getInstanceName () + ".json";
}