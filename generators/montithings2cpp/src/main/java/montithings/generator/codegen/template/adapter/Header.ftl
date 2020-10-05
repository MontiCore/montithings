<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("packageName", "compname", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
#pragma once
#include <string>
#include "Port.h"
#include <map>
#include <vector>
#include <list>
#include <set>
#include <utility>
#include "tl/optional.hpp"
${Utils.printIncludes(ComponentHelper.getImportStatements(compname,config))}

<@printNamespaceStart packageName />

class ${compname}AdapterTOP
{
private:
public:
  ${compname}AdapterTOP() = default;
  <#list ComponentHelper.getImportStatements(compname,config) as importStatement >
    virtual ${Utils.printCDType(importStatement)} convert(${importStatement.getImportClass()} element) = 0;
    virtual ${importStatement.getImportClass()} convert(${Utils.printCDType(importStatement)} element) = 0;
  </#list>
};
<@printNamespaceEnd packageName />

<#macro printNamespaceStart packages >
  namespace montithings {
    <#list packageName as package>
      namespace ${package} {
    </#list>
</#macro>

<#macro printNamespaceEnd packages >
    <#list packageName as package>
      } // namespace ${package}
    </#list>
  } // namespace montithings
</#macro>