<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/interface/helper/GeneralPreamble.ftl">
<#include "/template/Copyright.ftl">

#pragma once
#include "InOutPort.h"
#include "easyloggingpp/easylogging++.h"
#include "Message.h"
#include "collections/Set.h"

${Utils.printIncludes(comp, config)}

${Utils.printNamespaceStart(comp)}

${Utils.printTemplateArguments(comp)}
class ${className}
{
protected:
${tc.includeArgs("template.interface.helper.VariableDeclarations", [comp, config, existsHWC])}

public:
${tc.includeArgs("template.interface.helper.MethodDeclarations", [comp, config, existsHWC])}
};

<#if Utils.hasTypeParameter(comp)>
  ${tc.includeArgs("template.interface.Body", [comp, config, existsHWC])}
</#if>
${Utils.printNamespaceEnd(comp)}