<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("packageName", "compname", "config", "existsHWC")}
<#include "/template/adapter/helper/GeneralPreamble.ftl">

#pragma once
#include ${"<string>"}
#include "Port.h"
#include ${"<map>"}
#include ${"<vector>"}
#include ${"<list>"}
#include ${"<set>"}
#include ${"<utility>"}
#include "tl/optional.hpp"
#include "collections/Set.h"
#include "easyloggingpp/easylogging++.h"
${Utils.printIncludes(escape, ComponentHelper.getImportStatements(compname,config))}

${tc.includeArgs("template.adapter.helper.NamespaceStart", [packageName])}

class ${className}
{
protected:
public:
${className}() = default;
<#list ComponentHelper.getImportStatements(compname,config) as importStatement >
  <#assign cdFullName = Utils.printCDType(importStatement)>
  <#assign cdSimpleName = cdFullName?keep_after_last("::")>
  virtual ${cdFullName} convert${cdSimpleName}(${importStatement.getImportClass()} element) = 0;
  virtual ${importStatement.getImportClass()} convert${cdSimpleName}(${cdFullName} element) = 0;
</#list>
};
${tc.includeArgs("template.adapter.helper.NamespaceEnd", [packageName])}
