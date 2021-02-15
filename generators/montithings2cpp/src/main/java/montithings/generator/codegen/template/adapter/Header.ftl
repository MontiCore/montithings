<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("packageName", "compname", "config", "existsHWC")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign escape = Utils.escapePackage(packageName)>
<#assign className = compname + "Adapter">
<#if existsHWC>
    <#assign className += "TOP">
</#if>

#pragma once
#include ${"<string>"}
#include "Port.h"
#include ${"<map>"}
#include ${"<vector>"}
#include ${"<list>"}
#include ${"<set>"}
#include ${"<utility>"}
#include "tl/optional.hpp"
#include "easyloggingpp/easylogging++.h"
${Utils.printIncludes(escape, ComponentHelper.getImportStatements(compname,config))}

${tc.includeArgs("template.adapter.printNamespaceStart", [packageName])}

class ${className}
{
protected:
public:
${className}() = default;
<#list ComponentHelper.getImportStatements(compname,config) as importStatement >
  virtual ${Utils.printCDType(importStatement)} convert(${importStatement.getImportClass()} element) = 0;
  virtual ${importStatement.getImportClass()} convert(${Utils.printCDType(importStatement)} element) = 0;
</#list>
};
${tc.includeArgs("template.adapter.printNamespaceEnd", [packageName])}
