<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign className = comp.getName() + "DDSParticipant">
<#if existsHWC>
    <#assign className += "TOP">
</#if>

#pragma once
#include ${"<ace/Log_Msg.h>"}
#include ${"<iostream>"}
#include ${"<string>"}
#include ${"<vector>"}
#include ${"<chrono>"}
#include ${"<thread>"}
#include "easyloggingpp/easylogging++.h"

#include ${"<dds/DdsDcpsInfrastructureC.h>"}
#include ${"<dds/DdsDcpsPublicationC.h>"}
#include ${"<dds/DCPS/Marked_Default_Qos.h>"}
#include ${"<dds/DCPS/Service_Participant.h>"}
#include ${"<dds/DCPS/StaticIncludes.h>"}

<#-- Load libraries for rtps_udp transport communication -->
<#if config.getSplittingMode().toString() != "DISTRIBUTED">
#include ${"<dds/DCPS/RTPS/RtpsDiscovery.h>"}
#include ${"<dds/DCPS/transport/rtps_udp/RtpsUdp.h>"}
</#if>

#include "DDSPort.h"
#include "DDSParticipant.h"
#include "${comp.getName()}.h"

${Utils.printNamespaceStart(comp)}

class ${className} : public DDSParticipant
{
protected:
  ${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.getName()}* comp;

  // Port which handles the connectors 
  std::unique_ptr${"<DDSPort<std::string>>"} connectorPortOut;
  std::unique_ptr${"<DDSPort<std::string>>"} connectorPortIn;

  <#if config.getRecordingMode().toString() == "ON">
    bool isRecording = true;
  <#else>
    bool isRecording = false;
  </#if>

public:
  ${className} (${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.getName()} *comp, int argc, char *argv[]);
  ~${className}() = default;

  /*
   * Initially create ports of this component
  */
  void initializePorts();

  bool tryInitializeDDS(int argc, char *argv[]);
  void publishConnectors();
  void onNewConnectors(std::string payload);

  std::string getInstanceName() override;
};

${Utils.printNamespaceEnd(comp)}