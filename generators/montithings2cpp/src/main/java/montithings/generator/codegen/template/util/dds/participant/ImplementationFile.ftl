<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/Preamble.ftl">
<#include "/template/Copyright.ftl">

#include "${compname}DDSParticipant.h"

${Utils.printNamespaceStart(comp)}

${tc.includeArgs("template.util.dds.participant.Body", [comp, config, existsHWC])}


${Utils.printNamespaceEnd(comp)}