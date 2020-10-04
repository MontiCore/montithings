# (c) https://github.com/MontiCore/monticore

#pragma once
#include "${comp.getName()}.h"
#include "ManagementCommunication.h"
#include "ManagementMessageProcessor.h"

${Utils.printNamespaceStart(comp)}

class ${comp.getName()}Manager : public ManagementMessageProcessor
{
protected:
montithings::hierarchy::${comp.getName()}* comp;
ManagementCommunication* comm;
std::string managementPort;
std::string communicationPort;
<#if config.getSplittingMode().toString() == "LOCAL">
  std::string portConfigFilePath;
</#if>

public:
${comp.getName()}Manager (${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.getName()} *comp, std::string managementPort, std::string communicationPort);

/*
 * Process management instructions from the enclosing component
 * Those are mostly connectors to other components
 */
void process (std::string msg) override;

/*
 * Initially create ports of this component
 */
void initializePorts ();

/*
 * Search for subcomponents
 * Tell subcomponents to which ports of other components they should connect
 */
void searchSubcomponents ();
};

${Utils.printNamespaceEnd(comp)}