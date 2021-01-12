<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("packageName", "compname", "config", "existsHWC")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign className = compname + "Adapter">
<#if existsHWC>
    <#assign className += "TOP">
</#if>

#include "${className}.h"

namespace montithings {
<#list packageName as package>
  namespace ${package} {
</#list>

<#list packageName as package>
  } // namespace ${package}
</#list>
} // namespace montithings
