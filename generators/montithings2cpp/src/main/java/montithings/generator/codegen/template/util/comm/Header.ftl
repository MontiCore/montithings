<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign className = comp.getName() + "Manager">
<#if existsHWC>
 <#assign className += "TOP">
</#if>

#pragma once
#include "${comp.getName()}.h"
#include "ManagementCommunication.h"
#include "ManagementMessageProcessor.h"

${Utils.printNamespaceStart(comp)}

class ${className} : public ManagementMessageProcessor
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