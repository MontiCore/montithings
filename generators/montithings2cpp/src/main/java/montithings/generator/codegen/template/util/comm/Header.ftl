<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/util/comm/helper/GeneralPreamble.ftl">
<#include "/template/Copyright.ftl">

#pragma once
#include "${comp.getName()}.h"
#include "ManagementCommunication.h"
#include "ManagementMessageProcessor.h"
#include "easyloggingpp/easylogging++.h"

#include "Message.h"

${Utils.printNamespaceStart(comp)}

class ${className} : public ManagementMessageProcessor
{
protected:
${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.getName()}* comp;
ManagementCommunication* comm;
std::string managementPort;
std::string communicationPort;
<#if config.getSplittingMode().toString() == "LOCAL">
  std::string portConfigFilePath;
</#if>

public:
${className} (${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.getName()} *comp, std::string managementPort, std::string communicationPort);

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