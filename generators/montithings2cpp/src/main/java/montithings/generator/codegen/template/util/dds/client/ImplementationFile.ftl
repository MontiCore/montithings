<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/Preamble.ftl">
<#include "/template/Copyright.ftl">

#include "${compname}DDSClient.h"

${Utils.printNamespaceStart(comp)}

${tc.includeArgs("template.util.dds.client.Body", [comp, config, existsHWC])}


${Utils.printNamespaceEnd(comp)}