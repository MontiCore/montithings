<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

#pragma once
#include ${"<ace/Log_Msg.h>"}
#include ${"<iostream>"}
#include ${"<string>"}
#include ${"<vector>"}
#include ${"<chrono>"}
#include ${"<thread>"}

#include ${"<dds/DdsDcpsInfrastructureC.h>"}
#include ${"<dds/DdsDcpsPublicationC.h>"}
#include ${"<dds/DCPS/Marked_Default_Qos.h>"}
#include ${"<dds/DCPS/Service_Participant.h>"}
#include ${"<dds/DCPS/StaticIncludes.h>"}

#include "DDSMessageTypeSupportImpl.h"
#include "DDSPort.h"
#include "DDSParticipant.h"
#include "${comp.getName()}.h"

${Utils.printNamespaceStart(comp)}

class ${comp.getName()}DDSParticipant : public DDSParticipant
{
protected:
  ${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.getName()}* comp;

  // Port which handles the connectors 
  std::unique_ptr${"<DDSPort<std::string>>"} connectorPortOut;
  std::unique_ptr${"<DDSPort<std::string>>"} connectorPortIn;

public:
  ${comp.getName()}DDSParticipant (${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.getName()} *comp, int argc, char *argv[]);
  ~${comp.getName()}DDSParticipant() {}

  /*
   * Initially create ports of this component
  */
  void initializePorts();

  bool tryInitializeDDS(int argc, char *argv[]);
  void publishConnectors();
  void onNewConnectors(std::string payload);
};

${Utils.printNamespaceEnd(comp)}