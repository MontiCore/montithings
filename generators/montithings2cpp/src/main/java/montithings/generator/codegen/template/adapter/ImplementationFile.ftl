<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("packageName", "compname", "config")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>

#include "${compname}AdapterTOP.h"

namespace montithings {
<#list packageName as package>
  namespace ${package} {
</#list>

<#list packageName as package>
  } // namespace ${package}
</#list>
} // namespace montithings
